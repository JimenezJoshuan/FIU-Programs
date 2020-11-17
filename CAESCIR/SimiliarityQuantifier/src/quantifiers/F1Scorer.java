package quantifiers;

import java.io.*;
import java.util.*;

import ioHandler.InputHandler;
import utility.PCEmotion;

public class F1Scorer {
	public static final String inputPath = System.getProperty("user.dir")+"/input/";
	public static HashMap<String,File[]> inputs = new HashMap<String,File[]>();
	
	
	public static void main(String[] args) throws IOException {
		for(File f: new File(inputPath).listFiles()) {
			String fileName = f.getName();
			if(!fileName.endsWith(".xlsx") || fileName.startsWith("~"))
				continue;//skipping irrelevant files
			if(!inputs.containsKey(fileName.substring(0, fileName.length()-8))) {
				inputs.put(fileName.substring(0,fileName.length()-8),new File[2]);
			}
			if(fileName.substring(fileName.length()-7,fileName.length()-5).equalsIgnoreCase("sz"))
				inputs.get(fileName.substring(0,fileName.length()-8))[0] = f;
			else if(fileName.substring(fileName.length()-7,fileName.length()-5).equalsIgnoreCase("jj"))
				inputs.get(fileName.substring(0,fileName.length()-8))[1] = f;
			else
				System.out.println("Error: Illegal File found in input folder: "+f.getName());
		}
		for(String s: inputs.keySet()) {
			System.out.println("Measures for emotion spans of Novel "+s+": ");
			HashMap<String,Double> stats1 = evaluateF1forEmotionSpan(inputs.get(s));
			show(stats1);
			HashMap<String,Double> stats2 = evaluateF1forCharacters(inputs.get(s));
			System.out.println("Measures for characters of Novel "+s+": ");
			show(stats2);
			HashMap<String,Double> stats3 = evaluateCohensKappaforEmotionLabels(inputs.get(s));
			System.out.println("Measures for emotion labels of Novel "+s+": ");
			show(stats3);
			System.out.println("*******************************");
			System.out.println("/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/\\/");
		}
	}
	
	
	
	private static void show(HashMap<String, Double> stats) {
		System.out.println("_______________________________");
		for(String s: stats.keySet())
			if(s.equals("fn") || s.equals("fp") || s.equals("tp") )
				System.out.printf("%-20s\t%.0f\n",s,stats.get(s));
			else
				System.out.printf("%-20s\t%.5f\n",s,stats.get(s));
		System.out.println("_______________________________");
	}



	private static HashMap<String, Double> evaluateCohensKappaforEmotionLabels(File[] files) throws IOException {
		ArrayList<String> header0 = InputHandler.readFileHeader(files[0]);
		ArrayList<String> header1 = InputHandler.readFileHeader(files[1]);
		ArrayList<HashMap<String,String>> content0 = InputHandler.readFileContent(files[0]);
		ArrayList<HashMap<String,String>> content1 = InputHandler.readFileContent(files[1]);
		if(header0.size()!= header1.size()) {
			System.out.println("\nERROR: SZ file has "+header0.size()+" headers, but JJ file has "+header1.size()+" headers!!");
			return null;//error
		}
		for(int i =0; i < header1.size();i++)
			if(!header0.get(i).equals(header1.get(i))) {
				System.out.println("\nERROR: Headers at column "+((i<26)?(""):(""+Character.toString('A'+i/26)))+Character.toString('A'+i%26)+" don't match");
				return null;//error
			}
		if(content0.size()!= content1.size()) {
			System.out.println("\nERROR: SZ file has "+content0.size()+" sentences, but JJ file has "+content1.size()+" sentences!!");
			while(content0.size()<content1.size())
				content1.remove(content1.size()-1);
			while(content0.size()>content1.size())
				content0.remove(content0.size()-1);
		}
		double n = 0,acc = 0; 
		HashMap<PCEmotion,Double> tp = new HashMap<PCEmotion,Double>();
		HashMap<PCEmotion,Double> fp = new HashMap<PCEmotion,Double>();
		HashMap<PCEmotion,Double> fn = new HashMap<PCEmotion,Double>();
		HashMap<PCEmotion,Double> num0 = new HashMap<PCEmotion,Double>();
		HashMap<PCEmotion,Double> num1 = new HashMap<PCEmotion,Double>();
		PCEmotion[] arr = new PCEmotion[] {new PCEmotion("sadness"),new PCEmotion("anger"),
				new PCEmotion("anticipation"),new PCEmotion("trust"),
				new PCEmotion("fear"),new PCEmotion("disgust"),
				new PCEmotion("surprise"),new PCEmotion("joy")};
		for(PCEmotion p: arr) {
			tp.put(p,0.0);fp.put(p,0.0);fn.put(p,0.0);num0.put(p,0.0);num1.put(p,0.0);
		}
		
		
		
		for(int i = 0; i < content0.size();i++) {
			ArrayList<String> szSpans = new ArrayList<String>();
			ArrayList<String> jjSpans = new ArrayList<String>();
			ArrayList<String> matchedSpans0 = new ArrayList<String>();
			ArrayList<String> matchedSpans1 = new ArrayList<String>();
			for(String s: header0) {
				if(!s.startsWith("Emotion Span"))
					continue;
				if(content0.get(i).containsKey(s))
					if(content0.get(i).get(s).length() != 0)
						szSpans.add(content0.get(i).get(s));
				if(content1.get(i).containsKey(s))
					if(content1.get(i).get(s).length() != 0)
						jjSpans.add(content1.get(i).get(s));			
			}
			for(String sp0: szSpans) {
				double maxSim = 0;
				String temp0 = "",temp1 = "";
				for(String sp1: jjSpans)
					if(sim(sp0,sp1) > maxSim) {
						maxSim = sim(sp0,sp1);
						temp1 = sp1;
						temp0 = sp0;
					}
				if(maxSim != 0){//matched (partially or completely...)
					jjSpans.remove(temp1);//so that it doesn't match with other Emotion Spans...
					matchedSpans1.add(temp1);
					matchedSpans0.add(temp0);
				}
			}
			n+=matchedSpans0.size();//update the total number of matched spans...
			for(int j = 0; j < matchedSpans0.size();j++) {
				int ktemp0 = -1,ktemp1 = -1;
				for(int k = 2; k<header0.size();k+=8)
					if(content0.get(i).containsKey(header0.get(k)))
						if(content0.get(i).get(header0.get(k)).equals(matchedSpans0.get(j))) {
							ktemp0 = k;
							break;
						}
				for(int k = 2; k<header1.size();k+=8)
					if(content1.get(i).containsKey(header1.get(k)))
						if(content1.get(i).get(header1.get(k)).equals(matchedSpans1.get(j))) {
							ktemp1 = k;
							break;
						}
				HashSet<PCEmotion>set0 = new HashSet<PCEmotion>();
				HashSet<PCEmotion>set1 = new HashSet<PCEmotion>();
				for(int l = ktemp0+1; l < ktemp0+7;l++) {
					if(!content0.get(i).containsKey(header0.get(l)))
						continue;
					if(content0.get(i).get(header0.get(l)).length() == 0)
						continue;
					PCEmotion p = new PCEmotion(content0.get(i).get(header0.get(l)));
					if(p.getEmotion() == null)
						System.out.println("ERROR: in SZ file, row,col: "+(i+2)+", "+((l<26)?(""):(""+Character.toString('A'+l/26)))+Character.toString('A'+l%26)+": content is "+content0.get(i).get(header0.get(l)));
					else {
						set0.add(p);
						num0.put(p,num0.get(p)+1);
					}
				}
				for(int l = ktemp1+1; l < ktemp1+7;l++) {
					if(!content1.get(i).containsKey(header1.get(l)))
						continue;
					if(content1.get(i).get(header1.get(l)).length() == 0)
						continue;
					PCEmotion p = new PCEmotion(content1.get(i).get(header1.get(l)));
					if(p.getEmotion() == null)
						System.out.println("ERROR: in JJ file, row,col: "+(i+2)+", "+((l<26)?(""):(""+Character.toString('A'+l/26)))+Character.toString('A'+l%26));
					else {
						set1.add(p);
						num1.put(p,num1.get(p)+1);
					}
				}
				acc+=labelSim(set0,set1);
				for(PCEmotion p: set0)
					if(set1.contains(p))
						tp.put(p,tp.get(p)+1);
					else
						fn.put(p,fn.get(p)+1);
				for(PCEmotion p: set1)
					if(!set0.contains(p))
						fp.put(p,fp.get(p)+1);
					
			}			
		}
		double po = acc/n;
		double pe = 0;
		for(PCEmotion p: arr)
			pe += (num0.get(p)/n)*(num1.get(p)/n);
		HashMap<String,Double> out = new HashMap<String,Double>();
		out.put("po", po);
		out.put("pe", pe);
		out.put("kappa", (po-pe)/(1-pe));
		double macroF = 0,sumTP = 0,sumFP = 0, sumFN = 0;
		for(PCEmotion p: arr) {
			double f = tp.get(p)/(tp.get(p)+fn.get(p)/2+fp.get(p)/2);
			out.put(p+" F1",f);
			macroF += f;
			sumTP += tp.get(p);
			sumFP += fp.get(p);
			sumFN += fn.get(p);
		}
		out.put("macro-F1",macroF/arr.length);
		out.put("micro-F1",sumTP/(sumTP+sumFN/2+sumFP/2));
		return out;
	}



	private static double labelSim(HashSet<PCEmotion> set0, HashSet<PCEmotion> set1) {
		int common = 0;
		PCEmotion[] arr = new PCEmotion[] {new PCEmotion("sadness"),new PCEmotion("anger"),//array of all possible emotions
				new PCEmotion("anticipation"),new PCEmotion("trust"),
				new PCEmotion("fear"),new PCEmotion("disgust"),
				new PCEmotion("surprise"),new PCEmotion("joy")};
		for(PCEmotion p: arr)
			if(set0.contains(p) && set1.contains(p) || !set0.contains(p) && !set1.contains(p))
				common++;
		return common/8.0;
	}



	private static HashMap<String,Double> evaluateF1forEmotionSpan(File[] files) throws IOException {
		ArrayList<String> header0 = InputHandler.readFileHeader(files[0]);
		ArrayList<String> header1 = InputHandler.readFileHeader(files[1]);
		ArrayList<HashMap<String,String>> content0 = InputHandler.readFileContent(files[0]);
		ArrayList<HashMap<String,String>> content1 = InputHandler.readFileContent(files[1]);
		if(header0.size()!= header1.size()) {
			System.out.println("\nERROR: SZ file has "+header0.size()+" headers, but JJ file has "+header1.size()+" headers!!");
			return null;//error
		}
		for(int i =0; i < header1.size();i++)
			if(!header0.get(i).equals(header1.get(i))) {
				System.out.println("\nERROR: Headers at column "+((i<26)?(""):(""+Character.toString('A'+i/26)))+Character.toString('A'+i%26)+" don't match");
				return null;//error
			}
		if(content0.size()!= content1.size()) {
			System.out.println("\nERROR: SZ file has "+content0.size()+" sentences, but JJ file has "+content1.size()+" sentences!!");
			while(content0.size()<content1.size())
				content1.remove(content1.size()-1);
			while(content0.size()>content1.size())
				content0.remove(content0.size()-1);
		}
		double tp=0, fp=0, fn=0; 
		for(int i = 0; i < content0.size();i++) {
			ArrayList<String> szSpans = new ArrayList<String>();
			ArrayList<String> jjSpans = new ArrayList<String>();
			for(String s: header0) {
				if(!s.startsWith("Emotion Span"))
					continue;
				if(content0.get(i).containsKey(s))
					if(content0.get(i).get(s).length() != 0)
						szSpans.add(content0.get(i).get(s));
				if(content1.get(i).containsKey(s))
					if(content1.get(i).get(s).length() != 0)
						jjSpans.add(content1.get(i).get(s));			
			}
			for(String sp0: szSpans) {
				double maxSim = 0;
				String temp = "";
				for(String sp1: jjSpans)
					if(sim(sp0,sp1) > maxSim) {
						maxSim = sim(sp0,sp1);
						temp = sp1;
					}
				if(maxSim == 0)//not matched...
					fn++;
				else {//matched
					tp+=maxSim;
					jjSpans.remove(temp);//so that it doesn't match with other Emotion Spans...
				}
			}
			fp += jjSpans.size();
			/*for(String sp1: jjSpans) {
				double maxSim = 0;
				for(String sp0: szSpans)
					if(sim(sp0,sp1) > maxSim)
						maxSim = sim(sp0,sp1);
				if(maxSim == 0)
					fp++;
			}*/
		}
		HashMap<String,Double> out = new HashMap<String,Double>();
		out.put("tp", tp);
		out.put("fn", fn);
		out.put("fp", fp);
		out.put("f1", tp/(tp+fn/2+fp/2));
		return out;
	}
	
	private static HashMap<String,Double> evaluateF1forCharacters(File[] files) throws IOException {
		ArrayList<String> header0 = InputHandler.readFileHeader(files[0]);
		ArrayList<String> header1 = InputHandler.readFileHeader(files[1]);
		ArrayList<HashMap<String,String>> content0 = InputHandler.readFileContent(files[0]);
		ArrayList<HashMap<String,String>> content1 = InputHandler.readFileContent(files[1]);
		if(header0.size()!= header1.size()) {
			System.out.println("\nERROR: SZ file has "+header0.size()+" headers, but JJ file has "+header1.size()+" headers!!");
			return null;//error
		}
		for(int i =0; i < header1.size();i++)
			if(!header0.get(i).equals(header1.get(i))) {
				System.out.println("\nERROR: Headers at column "+((i<26)?(""):(""+Character.toString('A'+i/26)))+Character.toString('A'+i%26)+" don't match");
				return null;//error
			}
		if(content0.size()!= content1.size()) {
			System.out.println("\nERROR: SZ file has "+content0.size()+" sentences, but JJ file has "+content1.size()+" sentences!!");
			while(content0.size()<content1.size())
				content1.remove(content1.size()-1);
			while(content0.size()>content1.size())
				content0.remove(content0.size()-1);
		}
		double tp=0, fp=0, fn=0; 
		for(int i = 0; i < content0.size();i++) {
			ArrayList<String> szSpans = new ArrayList<String>();
			ArrayList<String> jjSpans = new ArrayList<String>();
			for(String s: header0) {
				if(!s.startsWith("Character"))
					continue;
				if(content0.get(i).containsKey(s))
					if(content0.get(i).get(s).length() != 0)
						if(!content0.get(i).get(s).contains("("))
							szSpans.add(content0.get(i).get(s));
						else
							szSpans.add(content0.get(i).get(s).substring(0,content0.get(i).get(s).indexOf('(')-1));
				if(content1.get(i).containsKey(s))
					if(content1.get(i).get(s).length() != 0)
						if(!content1.get(i).get(s).contains("("))
							jjSpans.add(content1.get(i).get(s));
						else
							jjSpans.add(content1.get(i).get(s).substring(0,content1.get(i).get(s).indexOf('(')-1));			
			}
			for(String sp0: szSpans) {
				double maxSim = 0;
				String temp = "";
				for(String sp1: jjSpans)
					if(sim(sp0,sp1) > maxSim) {
						maxSim = sim(sp0,sp1);
						temp = sp1;
					}
				if(maxSim == 0)//not matched...
					fn++;
				else{//matched...
					tp+=maxSim;
					jjSpans.remove(temp);//so that it doesn't match with other Emotion Spans...
				}
			}
			fp += jjSpans.size();
			/*for(String sp1: jjSpans) {
				double maxSim = 0;
				for(String sp0: szSpans)
					if(sim(sp0,sp1) > maxSim)
						maxSim = sim(sp0,sp1);
				if(maxSim == 0)
					fp++;
			}*/
		}
		HashMap<String,Double> out = new HashMap<String,Double>();
		out.put("tp", tp);
		out.put("fn", fn);
		out.put("fp", fp);
		out.put("f1", tp/(tp+fn/2+fp/2));
		return out;
	}
	private static double sim(String sp0, String sp1) {
		String[] s0 = sp0.split(" ");//tokenized
		String[] s1 = sp1.split(" ");
		if(s0.length+s1.length == 0)//both strings are empty
			return 1;
		boolean[] s1tick = new boolean[s1.length];//matched flag for every token in s1 to avoid double matching
		for(int i = 0; i < s1.length;i++)
			s1tick[i] = false;
		double matched = 0;
		for(int i = 0; i < s0.length;i++)
			for(int j = 0; j < s1.length;j++)
				if(s0[i].equalsIgnoreCase(s1[j])) {
					if(s1tick[j])
						continue;
					matched++;
					s1tick[j] = true;
					break;
				}
		if(s0.length<s1.length)
			return matched/s1.length;
		else
			return matched/s0.length;
	}
}