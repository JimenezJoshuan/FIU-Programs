package utility;

import java.util.Scanner;

public class PCEmotion {
	enum Emotion{
		JOY,
		SADNESS,
		FEAR,
		DISGUST,
		ANGER,
		ANTICIPATION,
		TRUST,
		SURPRISE
	}
	Emotion e;
	public PCEmotion(String emotion) {
		Scanner s = new Scanner(emotion);
		emotion = s.next();
		s.close();
		char[] emo = emotion.toLowerCase().toCharArray();
		switch(emo[0]) {
		case 'j':
			e = emotion.equalsIgnoreCase("joy")?Emotion.JOY:null;
			break;
		case 's':
			e = emotion.equalsIgnoreCase("sadness")?Emotion.SADNESS:emotion.equalsIgnoreCase("surprise")?
					Emotion.SURPRISE:null;
			break;
		case 'f':
			e = emotion.equalsIgnoreCase("fear")?Emotion.FEAR:null;
			break;
		case 'd':
			e = emotion.equalsIgnoreCase("disgust")?Emotion.DISGUST:null;
			break;
		case 'a':
			e = emotion.equalsIgnoreCase("anger")?Emotion.ANGER:emotion.equalsIgnoreCase("anticipation")?
					Emotion.ANTICIPATION:null;
			break;
		case 't':
			e = emotion.equalsIgnoreCase("trust")?Emotion.TRUST:null;
			break;
		default:
			e = null;
		}
	}
	public String getEmotion() {
		if(e == null)
			return null;
		switch(e) {
		case JOY:return "joy";
		case SADNESS: return "sadness";
		case SURPRISE: return "surprise";
		case TRUST: return "trust";
		case ANTICIPATION: return "anticipation";
		case DISGUST: return "disgust";
		case ANGER: return "anger";
		case FEAR: return "fear";
		default:
			return "null";
		}
	}
	public String toString() {
		switch(e) {
		case JOY:return "joy";
		case SADNESS: return "sadness";
		case SURPRISE: return "surprise";
		case TRUST: return "trust";
		case ANTICIPATION: return "anticipation";
		case DISGUST: return "disgust";
		case ANGER: return "anger";
		case FEAR: return "fear";
		default:
			return "null";
		}
	}
	public boolean equals(Object o) {//override the equals method of Object class
		return e == ((PCEmotion)o).e;
	}
	public int hashCode() {
		return e.hashCode();
	}
}