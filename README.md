# UnorderedParsing
<!-- (-   [Overview](#overview) -->
-   [Prerequisites](#prerequisites)
-   [Running Unordered Parsing](#running)
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
<a name='prerequisites'>Prerequisites</a>
--------

[Java Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
8 or higher.

<a name='running'>Running Unordered Parsing</a>
--------

- Download the jar file `UnorderedParsing.jar` in [releases.](https://github.com/GaliaZim/UnorderedParsing/releases)
- Run the program:  
  In the terminal (linux) or cmd (windows) type:
```
java -jar .\UnorderedParsing.jar [mode] [options]
```
## Modes:
There are two modes:
- **single** derives one genome against one PQ-tree and outputs the result to the console.
To run in single mode use ```java -jar .\UnorderedParsing.jar single [options]```
- **batch** derives every given genome against every given PQ-tree and outputs the results to a directory in which there is a file for every PQ-tree.
To run in batch mode use ```java -jar .\UnorderedParsing.jar batch [options]```

## <a name=single_options>Single Mode Options:</a>
### Mandatory:
- An argument for the PQ-tree using one of the following options:
  - **-j** PQT_JSON_FILE_NAME      
  Input file relative or absolute path containing the JSON representation of the PQ-tree (see format [here](#json_pqt)).
  - **-p** PARENTHESIS_REPRESENTATION_OF_PQT       
  The PQ-tree in its parenthesis representation (see format [here](#paren))
      
- An argument for the gene sequence using one of the following options:  
  - **-g** GENE_SEQUENSE_AS_STRING      
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
Input file containing the genome to parse as a JSON array of strings. Each string is a gene.

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
-- A P-node that has three leaf children with labels COG1, COG2, and COG3.
-- A Q-node that has two leaf children with labels COG1 and COG4.
-- A leaf with a label COG4
This is the same tree as in the JSON representation example
> ((COG1 COG2 COG3) [COG1 COG4] COG4)
* The same PQ-tree as in the last example, but quoted, so it can be sent as an argument to the program.
> "((COG1 COG2 COG3) [COG1 COG4] COG4)"

### <a name='genome'>Input Genome as String</a>
In the string representation of the genome the genes of the genome are written in order, separated by a single space.

This representation is given as input with the input option **-g** when running the program in **single** mode.
To pass this representation as input to the program it needs to be quoted.
#### Examples
* A String representing a genome that have 5 genes (the same genome as in the JSON represantation of the genome).
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
-- (LEAF_LABEL,GENE[GENE_INDEX]) if the leaf is mapped to GENEat index GENE_INDEX in the derivation
-- (LEAF_LABEL,DEL) if the leaf is deleted in the derivation.
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
   -- (LEAF_LABEL,GENE[GENE_INDEX]) if the leaf is mapped to GENEat index GENE_INDEX in the derivation
   -- (LEAF_LABEL,DEL) if the leaf is deleted in the derivation.

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
The command below runs the program in single mode for the PQ-tree [COG0422 COG0352 ([COG2022 COG1060] COG0476)], the genome that can be found in "C:\data\genome_115.txt", one deletion allowed from the genome, two deletions allowed from the PQ-tree and the default substitution matrix.
The program will output all the possible derivations.
```
java -jar .\UnorderedParsing.jar single -ds 1 -o all -dt 2 -p "[COG0422 COG0352 ([COG2022 COG1060] COG0476)]" -gf "C:\data\genome_115.txt"
```
The command below runs the program in single mode for the PQ-tree that can be found in "C:\data\pqt_35.txt", the genome COG0422 COG0352 COG1060 COG2022 COG0476, no deletion allowed and the substitution matrix that can be found in C:\data\substitution_matrix.txt.
The program will output only the best derivation.
```
java -jar .\UnorderedParsing.jar single -o best -j "C:\data\pqt_35.txt" -g "COG0422 COG0352 COG1060 COG2022 COG0476" -m "C:\data\substitution_matrix.txt"
```

The command below runs the program in batch mode for the PQ-trees that can be found in "C:\data\pqts.txt", the genome that can be found in "C:\data\genomes.txt", no deletion allowed and the default substitution matrix.
The program will output only the best derivation for every genome given that it has a score higher than 5.
```
java -jar .\UnorderedParsing.jar batch -o best -j "C:\data\pqts.txt" -g "C:\data\genomes.txt" -t 5
```

The command below runs the program in batch mode for the PQ-trees that can be found in "C:\data\pqts.txt", the genome that can be found in "C:\data\genomes.txt", no deletion allowed and the default substitution matrix.
The program will output all the derivations that have a score higher than 5.
The genomes will be treated as circular.
```
java -jar .\UnorderedParsing.jar batch -o all -j "C:\data\pqts.txt" -gc "C:\data\genomes.txt" -t 5
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
