# Data and Code "Confirming the Generalizability of a Chain-Based Animacy Detector"

Date: August 4, 2020

This archive contains the code and data for reproducing the results from Jahan et al. (2020) "Confirming the Generalizability of a Chain-Based Animacy Detector", published in the AI4Narratives workshop. The camera-ready version of the paper is included in the root of the archive. The archive is a snapshot of an Eclipse v2019-12 (4.14.0) Java 1.8 project, and can be directly imported into Eclipse using the Eclipse "Import Existing Project" function. The archive contains .project and .classfile files in the root for this purpose. All files are encoded in UTF-8, and uses Windows line endings. The contents of the rest of the archive are as follows:

## Citation

Jahan, L., Yarlott, W. V. H., Mittal, R., & Finlayson, M. A. (2020) "Confirming the Generalizability of a Chain-Based Animacy Detector". in *Proceedings of the 1st AI4Narratives Workshop*, Yokohama, Japan.

## Dependencies

**Note:** This code was developed on Windows 7.

**Programming Languages**

- [java 1.8](https://www.java.com/)
	- All of the code used in this project depends on Java 1.8

**Major Data and Code (included)**
- [Full Stanford CoreNLP 3.9.2](https://stanfordnlp.github.io/CoreNLP/download.html)
	- This is included as a separate download named ``stanford-corenlp-full-2018-10-05.zip`` that should be unpacked into ``$project$/lib/stanford-corenlp-full-2018-10-05``
- 113 files from the dataset used by Jahan, et al. (2018), including the ProppLearner corpus and part of the N2Corpus that has been approved for release, stored under ``$project$/data/ProppLearner`` and ``$project$/data/N2Corpus_NonFOUO``. In addition, a full annotated version of these files is found under ``$project/data/jahan_animacy_corpus_v1.0.0``.
- A selection from 30 novels from the Corpus of English Novels. The raw text is found under ``$project$/data/Annotated_CEN/TextFiles`` and the coreference-level animacy annotations are included under ``$project$/data/Annotated_CEN/AnnotatedChains``.

**Major Data (unincluded)**
- The 29 OpenSource.gov files from the N2Corpus, which are still classified FOUO and have not yet been approved for release, which we could not include. The intermediate output files for those corpus documents are included. These files should be unpacked into ``$project$/data/N2Corpus_FOUO``, overwriting the stub files contained within.
- The Ontonotes corpus, which we cannot include due to licensing. These files should be unpacked into ``$project$/data/AnnotatedOntonotesData/OntonoteTexts``, overwriting the stub files contained within. The coreference-level animacy annotations are included, under ``$project$/data/AnnotatedOntonotesData/OntonoteCoref``.

## Quick Start instructions
**Note:** If all you want to do is reproduce the results in the paper, you can use the quick start instructions. The quick start instructions assume you have all the data and intermediate generated files that are included in the archive.
1. Install Java 1.8
	- Java is required to run this program, and it is recommend to load this project in Eclipse.

2. Unzip the files
	- Unzip the main archive to the ``$project$`` directory.
	- Unzip ``stanford-corenlp-full-2018-10-05.zip`` into ``$project$/lib/stanford-corenlp-full-2018-10-05``

3. Uncomment the methods for the experiments desired to be rerun in the main method of ``$project$/src/Main.java`` and run the main method.

## Starting from Data and Generating Features
Follow steps 1 and 2 above.

4. Run the ``InitializeOutputFolder.initializeOutputFolder(String intermediatePath)`` method to generate the scaffolding for generating intermediate features.

5. Following the format examples from the included intermediate files, populate the CorefTags folder with the coreference chains for your data, populate the RefExp and RefExpID folders with the referring expressions and their IDs as used in CorefTags, respectively, and populate RefLabel with the per-referring expression animacy labels.

6. Follow the examples in one of the ``run-XXX-CVExperiments()`` methods, where -XXX- is one of the types of data, to generate the features and run cross-validation.

## Starting from Data without Labels
**Note:** this is intended for cases when the data does not include animacy annotation.

Follow steps 1, 2, 4, and 5 (without adding animacy labels).

7. Load a model using the ``RefSVMPipeline.loadSVMModel(String filename)`` method. There are three models included under ``$project$/cachedModels``

8. Follow the examples in the ``run-XXX-CrossCorpusExperiments()`` methods for using pre-trained models.

## Description of General Structure

- **/bin/** - Compiled class files
- **/cachedModels/** - Cached, pre-trained SVM models on each of the three individual corpora described in the paper.
- **/data/** - All of the data used in this project, with the exceptions noted above.
- **/lib/** - The libraries used in this project, with the exception of Stanford CoreNLP.
- **/output/** - The intermediate files used to run the models.
- **/resources/** - Binary files needed for some of the libraries used to extract features.
- **/src/** - The source code.
