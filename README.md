# PQFinder
<!-- (-   [Overview](#overview) -->
-   [Supplement Material](#supmat)
    -	[Quick Demonstration](#demo)
    -	[Reconstructing the Results](#reconstruct)
    -	[Results](#results)
-   [Prerequisites](#prerequisites)
-   [Running PQFinder](#running)
    -   [Single Mode Options](#single_options)
    -   [Batch Mode Options](#batch_options)
-   [Input Formats](#input)
    -   [Input PQ-tree as a JSON File](#json_pqt)
    -   [Input Genome as a JSON file](#json_genome)
    -   [Input PQ-tree in the Parenthesis Representation](#paren)
    -   [Input Genome as String](#genome)
    -   [Input a List of PQ-trees in File](#pqts_file)
    -   [Input a list of Genomes in File](#genomes_file)
    -   [Input File Containing the Substitution Matrix](#subs)
-   [Output Formats](#output)
    -   [Single Mode](#single_out)
    -   [Batch Mode](#batch_out)
-   [Running Examples](#examples)
<!--
-   [License](#license)
-   [Author](#author)
-   [Credit](#credit)
-->

<!--
<a name='overview'>Overview</a>
--------
### TODO
-->
<a name='supmat'>Supplement Material</a>
--------
This section is for the reader of the paper **Approximate Search for Known Gene Clusters in New Genomes Using PQ-Trees**.
You can find in this section two small running examples ([here](#demo)), all the materials needed to reconstruct our
results ([here](#reconstruct)) and references to the supplement material mentioned in the paper ([here](#results)).
For more detailed information on how to run the tool and the different input and output options read the rest of this README file.

### <a name='demo'>Quick Demonstration</a>
To run the tool you need:
1. [Java Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
8 or higher
2. Downloaded the latest version jar file `PQFinder.jar` in [releases.](https://github.com/GaliaZim/PQFinder/releases)

Here are two running examples: (Run them in the terminal (linux) or cmd (windows). The output will be printed terminal/cmd in the format described [here](#single_out))
```
java -jar .\PQFinder.jar single -p "[[COG0642 COG0745] [[COG3696 COG0845] COG1538]]" -g "COG2020 COG1538 COG0845 COG3696 COG1230 COG0745 COG0642 COG3203 COG2801 COG2963" -o best -ds 1
```
```
java -jar .\PQFinder.jar single -p "[[COG0642 COG0745] [[COG3696 COG0845] COG1538]]" -g "COG2259 COG2823 COG1538 COG3696 COG0845 COG0745 COG0642 COG1538 COG0845 COG3696 X COG0531" -o all
```
For more detailed information on how to run the tool and the different input and output options read the rest of this README file.

### <a name='reconstruct'>Reconstructing the Results</a>
Note that this run takes about 2 hours. Shorter running examples are given under Quick Demonstration [above](#demo).  
- 1,487 fully sequenced prokaryotic strains with COG ID annotations were downloaded from GenBank (NCBI; ver 10/2012).  
- Among these strains, 471 genomes included a total of 933 plasmids.  
- **The file containing all plasmid genomes is [here](https://github.com/GaliaZim/PQFinder/blob/master/SupplementMaterial/plasmid_genomes.fasta).**  
- The gene clusters were generated from all the genomes in the dataset after removing their plasmids using the tool [CSBFinder-S](https://github.com/dinasv/CSBFinder).  
- The generation of PQ-trees was performed using [this program](https://github.com/levgou/pqtrees).  
- **The file containing all 779 generated PQ-trees in their [parenthesis representation](#paren) is [here](https://github.com/GaliaZim/PQFinder/blob/master/SupplementMaterial/dataset_pqt.txt).**
- **The cog-to-cog substitution function used is given as a matrix in this compressed [file](https://github.com/GaliaZim/PQFinder/blob/master/SupplementMaterial/cog-to-cog-substitution-matrix.rar).**

To reconstruct the results do the following:
1. Download the three files mentioned above (they can all be found in the [SupplementMaterial directory](https://github.com/GaliaZim/PQFinder/tree/master/SupplementMaterial)). Let us say that they are all on your machine in the path PATH_TO_DIR.
2. Extract the cog-to-cog substitution matrix.
3. Download [Java Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
8 or higher.
4. Download the latest version jar file `PQFinder.jar` in [releases.](https://github.com/GaliaZim/PQFinder/releases)
5. In the terminal (linux) or cmd (windows) type:
```
java -jar .\PQFinder.jar batch -p "PATH_TO_DIR\dataset_pqt.txt" -gc "PATH_TO_DIR\plasmid_genomes.fasta" -m "PATH_TO_DIR\cog-to-cog-substitution-matrix.txt -tf 0.825 -o all -dest "PATH_TO_DESTINATION_FOLDER"
```
The results will be in ```PATH_TO_DESTINATION_FOLDER``` in the format described [here](#batch_out).
**This run takes about 2 hours** (depending on your hardware). For a shorter run, remove plasmids from the plasmid_genomes file and remove PQ-trees from the dataset_pqt file, or run the examples under Quick Demonstration [above](#demo).

### <a name='results'>Results</a>
The materials described bellow can be found in the [SupplementMaterial directory](https://github.com/GaliaZim/PQFinder/tree/master/SupplementMaterial).
1. The results of the above run were processed into [this session file](https://github.com/GaliaZim/PQFinder/blob/master/SupplementMaterial/pqt-instances-found-in-plasmids.csb). To view it use [CSBFinder-S](https://github.com/dinasv/CSBFinder).
2. The generated PQ-trees along with some useful information is found in [this file](https://github.com/GaliaZim/PQFinder/blob/master/SupplementMaterial/dataset-pqtrees-information.xlsx). The data columns in the file are the following:
	- **family_id:** The id of the gene cluster that the PQ-tree represents
	- **#Members:** The number of distinct gene orders of the families found using CSBFinder-S.
	- **COG PQT:** The PQ-tree in parenthesis representation with the full COG ID of the genes.
	- **Category PQT:** The PQ-tree in parenthesis representation with every gene's COG replaced by its main category letter.
	- **O PQT:** The PQ-tree in parenthesis representation ignoring the genes. Every gene is represented as the letter O.
	- **S-score:** The S-score of the PQ-tree.
	- **Frontier length:** The number of leafs in the PQ-tree, the number of genes in the gene cluster.
	- **Consistent Frontier Size:** The size of the tree's consistent frontier.
	- **#appearances in plasmids dS=dT=0:** Number of plasmids that had at least one instance of the gene cluster without deletions from either string or tree. The information is retrieved from the run given [above](#reconstruct).
	- **#p  #q:** Number of P-nodes in the tree followed by the number of Q-nodes in the tree.
	- **Category:** The categories associated with the gene cluster separated by '\'.

<a name='prerequisites'>Prerequisites</a>
--------

[Java Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
8 or higher.

<a name='running'>Running PQFinder</a>
--------

- Download the latest version jar file `PQFinder.jar` in [releases.](https://github.com/GaliaZim/PQFinder/releases)
- Run the program:
  In the terminal (linux) or cmd (windows) type:
```
java -jar .\PQFinder.jar [mode] [options]
```
## Modes:
There are two modes:
- **single** derives one genome against one PQ-tree and outputs the result to the console.
To run in single mode use ```java -jar .\PQFinder.jar single [options]```
- **batch** derives every given genome against every given PQ-tree and outputs the results to a directory in which there is a file for every PQ-tree.
To run in batch mode use ```java -jar .\PQFinder.jar batch [options]```

## <a name=single_options>Single Mode Options:</a>
### Mandatory:
- An argument for the PQ-tree using one of the following options:
  - **-j** PQT_JSON_FILE_NAME
  Input file relative or absolute path containing the JSON representation of the PQ-tree (see format [here](#json_pqt)).
  - **-p** PARENTHESIS_REPRESENTATION_OF_PQT
  The PQ-tree in its parenthesis representation (see format [here](#paren))

- An argument for the gene sequence using one of the following options:
  - **-g** GENE_SEQUENCE_AS_STRING
   A string of the genes separated by a single space (see format [here](#genome))
  - **-gf** GENE_SEQUENCE_JSON_FILE
   Input file relative or absolute path containing the JSON representation of the gene sequence as described [here](#json_genome).

### Optional:
- **-m** PATH_TO_SUBSTITUTION_MATRIX_FILE
  Input file relative or absolute path containing the substitution matrix (see format [here](#subs)).
  **Default**: a substitution matrix that does not allow substitutions and rewards a match with a score of 1.

- **-dt** TREE_DELETION_LIMIT
  The number of allowed deletions from the tree. A non-negative number
**Default**: 0

- **-ds** STRING_DELETION_LIMIT
  The number of allowed deletions from the gene sequence. A non-negative number
  **Default**: 0

- **-o** OUTPUT_OPTION
  Possible values: [all, best]
  **all**: outputs all possible derivations in an descending order according to their score
  **best**: outputs only the best derivation
  **distinct**: outputs the best derivation for every start index and every end index (if one exists) in an descending order according to their score
  **Default**: best
  See the [output formats](#output) section for more information.


## <a name=batch_options>Batch Mode Options:</a>
### Mandatory:
- **-p** PQTS_FILE_NAME
Input file relative or absolute path containing a list of PQ-trees in their parenthesis representation and their IDs (see format [here](#pqts_file)).

- An argument for the gene sequence using one of the following options:
  - **-g** GENOMES_FILE
   Input file relative or absolute path containing a list of genomes and their IDs as described [here](#genomes_file).
  - **-gc** GENOMES_FILE
   The same input as the -g option, but this option indicates the program should treat the genomes as circular.

- **-dest** OUTPUT_DIRECTORY
Output directory relative or absolute path. The output of the program will be saved to this directory.

### Optional:
- **-m** PATH_TO_SUBSTITUTION_MATRIX_FILE
  Input file relative or absolute path containing the substitution matrix (see format [here](#subs)).
  **Default**: a substitution matrix that does not allow substitutions and rewards a match with a score of 1.

- A tree deletion limit argument using one of the following.
    - **-dt** TREE_DELETION_LIMIT
    The number of allowed deletions from the tree. A non-negative number
    - **-dtf** TREE_DELETION_FACTOR
    determines the tree deletion limit according to the span of the PQ-tree. TREE_DELETION_FACTOR is a non-negative number
    tree_deletion_limit = ceil(PQ-tree_span / TREE_DELETION_FACTOR)
    - **Default**: -dt 0

- A string deletion limit argument using one of the following.
    - **-ds** STRING_DELETION_LIMIT
    The number of allowed deletions from the gene sequence. A non-negative number
    - **-dsf** STRING_DELETION_FACTOR
    determines the tree deletion limit according to the span of the PQ-tree. STRING_DELETION_FACTOR is a non-negative number
    string_deletion_limit = ceil(PQ-tree_span / STRING_DELETION_FACTOR)
    -  **Default**: -ds 0

- **-o** OUTPUT_OPTION
  Possible values: [all, best, distinct]
  **all**: for every PQ-tree and genome outputs all possible derivations in an descending order according to their score
  **best**: for every PQ-tree and genome outputs only the best derivation
  **distinct**: for every PQ-tree and genome outputs the best derivation for every start index and every end index (if one exists) in an descending order according to their score
  **Default**: best
  See the [output formats](#output) section for more information.

- A threshold for the scores of the derivations, only derivations above this threshold will be outputted. The threshold is given using one of the following:
    - **-t** THRESHOLD
    The threshold. Any number can be given.
    - **-tf** THRESHOLD_FACTOR
    determines the threshold according to the span of the PQ-tree.
    THRESHOLD_FACTOR can be any number
    threshold = PQ-tree_span * THRESHOLD_FACTOR
    - **Default**: -t 0

<a name='input'>Input Formats</a>
--------
### <a name='json_pqt'>Input PQ-tree as a JSON File</a>
Input file containing the JSON representation of the PQ-tree.
The JSON representation of a PQ-tree is a JSON object with the key "root" and a JSON object with the **internal node format** or the **leaf format** (described below) as value.
#### Internal Node Format
two key-value pairs:
* Key "type" with one of the following values: "PNode" (if it is a P-node) or "QNode (if it is a Q-node).
* Key "children" that its value is an array of JSON objects with the **internal node format** or the **leaf format** (the array can contain both).
#### Leaf Format
two key-value pairs:
* Key "type" with a string value "Leaf"
* Key "cog" with a string value. This can be any string, and it represents the label of the leaf. It will determine the allowed mappings of the leaf and their score.

The path to this file is given as input with the input option **-j** when running the program in **single** mode.
#### Example
In the example below is the JSON format of a PQ-tree rooted in a P-node that has three children:
* A P-node that has three leaf children with labels COG1, COG2, and COG3.
* A Q-node that has two leaf children with labels COG1 and COG4.
* A leaf with a label COG4

The indentation is not important, and given here for convenience.
```json
{"root":
    {"type":"PNode",
    "children": [
        {"type":"PNode",
        "children": [
            {"type":"LEAF", "cog":"cog1"},
            {"type":"LEAF", "cog":"cog2"},
            {"type":"LEAF", "cog":"cog3"}
        ]},
        {"type":"QNode",
        "children": [
            {"type":"LEAF", "cog":"cog1"},
            {"type":"LEAF", "cog":"cog4"}
        ]},
        {"type":"LEAF", "cog":"cog4"}
    ]}
}
```
### <a name='json_genome'>Input Genome as a JSON file</a>
Input file containing the genome to derive as a JSON array of strings. Each string is a gene.

The path to this file is given as input with the input option **-gf** when running the program in **single** mode.
#### Example
In the example below is the content of the file representing a genome that has 5 genes.
```json
["COG1", "COG2", "COG3", "COG2", "COG5"]
```
### <a name='paren'>Input PQ-tree in the Parenthesis Representation</a>
In the parenthesis representation of a PQ-tree every internal node is written as a pair of matching brackets and between them the node's children in the parenthesis representation separated by a single space.
Square brackets [] represent a Q-node and round brackets () represent a P-node.
To represent a leaf, write its label.

This representation is given as input with the input option **-p** when running the program in **single** mode.
To pass this representation as input to the program it needs to be quoted.
#### Examples
In the first three examples we do not quote the PQ-tree.
* A **P-node** with three leaf children that have the labels COG1, COG2, and COG3.
> (COG1 COG2 COG3)
* A **Q-node** with three leaf children that have the labels COG1, COG2, and COG3.
> [COG1 COG2 COG3]
* A P-node that has three children:
	- A P-node that has three leaf children with labels COG1, COG2, and COG3.
	- A Q-node that has two leaf children with labels COG1 and COG4.
	- A leaf with a label COG4
This is the same tree as in the JSON representation example
> ((COG1 COG2 COG3) [COG1 COG4] COG4)
* The same PQ-tree as in the last example, but quoted, so it can be sent as an argument to the program.
> "((COG1 COG2 COG3) [COG1 COG4] COG4)"

### <a name='genome'>Input Genome as String</a>
In the string representation of the genome the genes of the genome are written in order, separated by a single space.

This representation is given as input with the input option **-g** when running the program in **single** mode.
To pass this representation as input to the program it needs to be quoted.
#### Examples
* A String representing a genome that have 5 genes (the same genome as in the JSON representation of the genome).
> COG1 COG2 COG3 COG2 COG5
* The same genome, but quoted so it can be sent as an argument to the program.
> "COG1 COG2 COG3 COG2 COG5"

### <a name='pqts_file'>Input a List of PQ-trees in File</a>
A file containing a line for every PQ-tree in the list.
Every line contains a unique identifier for the PQ-tree and the parenthesis representation of the Pq-tree separated by a TAB character.
```[PQ_TREE_ID][TAB][PARENTHESIS_REPRESENTATION][NEW_LINE]```
The parenthesis representation of a PQ-tree is described [here](#paren) (do not use quotes in the file).
The path to this file is given as input with the input option **-p** when running the program in **batch** mode.
#### Example
An example of a file containing 3 PQ-trees.
```
pqt_1	[COG5 COG3 (COG2 COG2 COG4)]
pqt_2	(COG2 [COG2 COG4] [COG1 COG6] COG3)
pqt_15	(COG1 [COG2 COG3 COG4] COG6)
```

### <a name='genomes_file'>Input a list of Genomes in File</a>
A file containing several lines for each genome in the list.
A genome must start with an ID line: a line that has a **>** symbol and then the ID of the genome.
```>[GENOME_ID]```
Then, each line contains one gene.
```
[GENE]
[GENE]
[GENE]
[GENE]
```

Each line can contain more information, as long as it is separated from the gene with a TAB character. Everything after the TAB character is ignored.
The path to this file is given as input with the input option **-g** or the input option **-gc**  when running the program in **batch** mode.

#### Example
A file containing 2 genomes: The first has the ID "genome_1" and 4 genes, and the second has the ID "genome_5" and 5 genes.
```
>genome_1
COG1
COG2
COG4
COG5
>genome_5
COG3	+
COG2	-
COG1
COG4	-
COG5	+
```

### <a name='subs'>Input File Containing the Substitution Matrix</a>
A symmetric matrix, where the first line and column are the labels of the PQ-tree leafs and the genes in the genome arranged in the same order. The labels and genes are separated by a TAB character. The score for a substitution is given as a number, or if the substitution is prohibited a dot symbol (.) is given.
##### A template of the substitution matrix
In this template S_1_2 represents the score for substitution GENE_1 with GENE_2.
>[TAB][GENE_1][TAB][GENE_2][TAB][GENE_3][TAB][GENE_4]
>[GENE_1][TAB][S_1_1][TAB][S_1_2][TAB][S_1_3][TAB][S_1_4]
>[GENE_2][TAB][S_2_1][TAB][S_2_2][TAB][S_2_3][TAB][S_2_4]
>[GENE_3][TAB][S_3_1][TAB][S_3_2][TAB][S_3_3][TAB][S_3_4]
>[GENE_4][TAB][S_4_1][TAB][S_4_2][TAB][S_4_3][TAB][S_4_4]

The path to this file is given as input with the input option **-m**.

If the matrix is not symmetric, or if there is a leaf label or a gene that are not given in the matrix, the program will give an error.
#### Example
The matrix below defines the substitution score for four labels and genes: COG1, COG2, COG3 and COG4.
* The substitution of COG1 and COG2 gives a score of 1
* A match, the substitution of a gene/label with itself gives a score of 1.5
* COG4 is not allowed to be substituted with any gene/label but itself
* All other substitutions give a score of 0.3
```
	COG1	COG2	COG3	COG4
COG1	1.5	0.3	1	.
COG2	0.3	1.5	0.3	.
COG3	1	0.3	1.5	.
COG4	.	.	.	1.5
```

<a name='output'>Output Formats</a>
--------
## <a name=single_out>Single Mode</a>
### One derivation
For one derivation the program outputs the following:
1. The score of the derivation
2. The derived substring and its indices in the genome (inclusive, starting from index 1)
3. The one-to-one mappings of the leafs of the PQ-tree from left to right. separated by a semicolon surrounded by space characters. A mapping is denoted as follows:
	- (LEAF_LABEL,GENE[GENE_INDEX]) if the leaf is mapped to GENEat index GENE_INDEX in the derivation
	- (LEAF_LABEL,DEL) if the leaf is deleted in the derivation.
4. The number of deleted genes, the genes and their indices
5. The number of deleted leafs and a list of their labels
#### Example
In the following output example, the output is a derivation with a score of 5.
The derived substring is COG1060, COG4545, COG2022, COG0476, COG0352, COG0123 and it appears in the genome between the indices 11 and 16 (inclusive).
The leftmost leaf in the PQ-tree has the label COG0422 and it is mapped to the gene COG0123 at index 16 in the genome.
The rightmost leaf in the PQ-tree has the label COG0555 and it is deleted in the derivation.
Two genes were deleted COG4545 and COG0476 in indices 12 and 14, respectively.

> Derivation score: 5.0
> The derived substring is S[11:16] = COG1060,COG4545,COG2022,COG0476,COG0352,COG0123
> The one-to-one mapping:
> (COG0422,COG0123[16]) ; (COG0352,COG0352[15]) ; (COG2022,COG2022[13]) ; (COG1060,COG1060[11]) ; (COG0555,DEL)
> 2 gene(s) deleted in the derivation:
> COG4545 at index 12
> COG0476 at index 14
> 1 leaf(s) deleted in the derivation:
> COG555

### All Possible Derivations
If the program was given the ```-o all``` option it will output all the derivations found (at most one derivation for every substring).
The derivations are given in the above format and in descending order with respect to the score of the derivations.
If 2 derivations have the same score, the one with less deletions is given first.

### Distinct Derivations
If the program was given the ```-o distinct``` option it will output the best derivations with distinct start and end indices.
That is, at most one derivation for every end index and at most one for every start index.
The derivations are given in the best format and in descending order with respect to the score of the derivations. If 2 derivations have the same score, the one with less deletions is given first.

## <a name=batch_out>Batch Mode</a>
The program creates a directory named "BatchJobResults_DATE" in the path given in the **-dest** option.
In the directory a text file is created for every PQ-tree given as input for which at leas one derivation was found.
The file name will be ```g_[NUMBER]_[PQ_TREE_ID].txt``` where NUMBER is the number of genomes that can be derived from
the PQ-tree.
The file contains the parenthesis representation of the PQ-tree, followed by a section for every genome that can be
derived from the PQ-tree.
Every genome section starts with a '>' symbol followed by the genome ID and the number of substrings in the genome that
can be derived to the PQ-tree separated by a TAB character.
```
>[GENOME_ID][TAB]found:[NUMBER]
```
If the **best** output option was given, the number of substrings is always 1.
Then the information for the derived substrings and their derivations is given. If the **best** output option was given,
only the information for the best derivation is given.
If the **all** output option was given, the program will output the best derivation for every substring
(as long as there is such a derivation and its score is higher than the threshold).
If the **distinct** output option was given, the program will output the best derivations with distinct start and end indices.
That is, at most one derivation for every end index and at most one for every start index.

For every substring the file will contain one line containing the following information separated by the TAB character.
1. The indices of the substring in the genome (inclusive, starting from index 1)
2. The score of the derivation
3. The number of deletions from the string
4. The number of deletions from the tree
5. The one-to-one mappings of the leafs of the PQ-tree from left to right. separated by a semicolon surrounded by space characters. A mapping is denoted as follows:
   - (LEAF_LABEL,GENE[GENE_INDEX]) if the leaf is mapped to GENEat index GENE_INDEX in the derivation
   - (LEAF_LABEL,DEL) if the leaf is deleted in the derivation.

```[[START_INDEX]:[END_INDEX]][TAB][SCORE][TAB]delS:[#STRING_DELETIONS][TAB]delT:[#TREE_DELETIONS][TAB][ONE_TO_ONE_MAPPING]```

If the **-gc** option is given to the program, the start index of the derived substring can be higher than the end index.
For example, if the genome is ```COG1 COG2 COG3 COG4 COG5 COG6``` and the indices of the substring are ```[5:2]```,
then it means the substring is ```COG5 COG6 COG1 COG2```.
#### Examples
- Output file when given the **best** output options and **-g**
```
(1 2 [3 4 5] 6 7)
>g1	found:1
[1:7]	6.000000	delS:1	delT:1	(1,1[1]) ; (2,2[2]) ; (3,3[5]) ; (4,4[3]) ; (5,DEL) ; (6,6[6]) ; (7,7[7])

>g2	found:1
[1:7]	6.000000	delS:1	delT:1	(1,1[2]) ; (2,2[3]) ; (3,3[7]) ; (4,4[6]) ; (5,5[5]) ; (6,6[1]) ; (7,DEL)
```

- Output file when given the **all** output options and **-g**
```
(1 2 [3 4 5] 6)
>g2	found:2
[1:7]	6.000000	delS:1	delT:0	(1,1[2]) ; (2,2[3]) ; (3,3[7]) ; (4,4[6]) ; (5,5[5]) ; (6,6[1])
[1:6]	5.000000	delS:1	delT:1	(1,1[2]) ; (2,2[3]) ; (3,DEL) ; (4,4[6]) ; (5,5[5]) ; (6,6[1])

>g1	found:7
[9:14]	5.000000	delS:1	delT:1	(1,1[9]) ; (2,2[10]) ; (3,3[13]) ; (4,4[11]) ; (5,DEL) ; (6,6[14])
[4:9]	5.000000	delS:1	delT:1	(1,1[9]) ; (2,2[8]) ; (3,3[5]) ; (4,DEL) ; (5,5[4]) ; (6,6[6])
[1:6]	5.000000	delS:1	delT:1	(1,1[1]) ; (2,2[2]) ; (3,3[5]) ; (4,4[3]) ; (5,DEL) ; (6,6[6])
[9:15]	5.000000	delS:2	delT:1	(1,1[9]) ; (2,2[10]) ; (3,3[13]) ; (4,4[11]) ; (5,DEL) ; (6,6[14])
[6:12]	5.000000	delS:2	delT:1	(1,1[9]) ; (2,2[8]) ; (3,DEL) ; (4,4[11]) ; (5,5[12]) ; (6,6[6])
[4:10]	5.000000	delS:2	delT:1	(1,1[9]) ; (2,2[10]) ; (3,3[5]) ; (4,DEL) ; (5,5[4]) ; (6,6[6])
[1:7]	5.000000	delS:2	delT:1	(1,1[1]) ; (2,2[2]) ; (3,3[5]) ; (4,4[3]) ; (5,DEL) ; (6,6[6])
```

- Output file when given the **best** output options and **-gc**
```
[1 2 (3 4 5) 6]
>g2	found:1
[2:1]	6.000000	delS:1	delT:0	(1,1[2]) ; (2,2[3]) ; (3,3[7]) ; (4,4[6]) ; (5,5[5]) ; (6,6[1])

>g1	found:1
[1:6]	6.000000	delS:0	delT:0	(1,1[1]) ; (2,2[2]) ; (3,3[5]) ; (4,4[3]) ; (5,5[4]) ; (6,6[6])
```

<a name='examples'>Running Examples</a>
--------  
**Note that the files refrenced in the below examples do not exist and they are not provided in this repository. They are given below as an example of how to send files as input to this tool.**

The command below runs the program in single mode for the PQ-tree [COG0422 COG0352 ([COG2022 COG1060] COG0476)], the genome that can be found in "C:\data\genome_115.txt", one deletion allowed from the genome, two deletions allowed from the PQ-tree and the default substitution matrix.
The program will output all the possible derivations.
```
java -jar .\PQFinder.jar single -ds 1 -o all -dt 2 -p "[COG0422 COG0352 ([COG2022 COG1060] COG0476)]" -gf "C:\data\genome_115.txt"
```
The command below runs the program in single mode for the PQ-tree that can be found in "C:\data\pqt_35.txt", the genome COG0422 COG0352 COG1060 COG2022 COG0476, no deletion allowed and the substitution matrix that can be found in C:\data\substitution_matrix.txt.
The program will output only the best derivation.
```
java -jar .\PQFinder.jar single -o best -j "C:\data\pqt_35.txt" -g "COG0422 COG0352 COG1060 COG2022 COG0476" -m "C:\data\substitution_matrix.txt"
```

The command below runs the program in batch mode for the PQ-trees that can be found in "C:\data\pqts.txt", the genome that can be found in "C:\data\genomes.txt", no deletion allowed and the default substitution matrix.
The program will output only the best derivation for every genome given that it has a score higher than 5.
```
java -jar .\PQFinder.jar batch -o best -j "C:\data\pqts.txt" -g "C:\data\genomes.txt" -t 5
```

The command below runs the program in batch mode for the PQ-trees that can be found in "C:\data\pqts.txt", the genome that can be found in "C:\data\genomes.txt", no deletion allowed and the default substitution matrix.
The program will output all the derivations that have a score higher than 5.
The genomes will be treated as circular.
```
java -jar .\PQFinder.jar batch -o all -j "C:\data\pqts.txt" -gc "C:\data\genomes.txt" -t 5
```
  
<!--
<a name='license'>License</a>
--------
### TODO
-->
<!--
<a name='author'>Author</a>
--------
### TODO
-->
<!--
<a name='credit'>Credit</a>
--------
### TODO
-->
