# UnorderedParsing
<!-- (-   [Overview](#overview) -->
-   [Prerequisites](#prerequisites)
-   [Running Unordered Parsing](#running)
-   [Input Formats](#input)
    -   [Input PQ-tree as a JSON File](#json_pqt)
    -   [Input Genome as a JSON file](#json_genome)
    -   [Input PQ-tree in the Parenthesis Represantation](#paren)
    -   [Input Genome as String](#genome)
    -   [Input File Containing the Substitution Matrix](#subs)
<!--
-   [Output Formats](#output)
-   [Running Examples](#examples)
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
java -jar .\UnorderedParsing.jar [options]
```

## Options:
### Mandatory:
- An argument for the PQ-tree using one of the following options:
  - **-j** PQT_JSON_FILE_NAME      
  Input file relative or absolute path containing the JSON representation of the PQ-tree.   
  - **-p** PARENTHESIS_REPRESENTATION_OF_PQT       
  The PQ-tree in its parenthesis representation  
      
- An argument for the gene sequence using one of the following options:  
  - **-g** GENE_SEQUENSE_AS_STRING      
   A string of the genes separated by a single space  
  - **-gf** GENE_SEQUENCE_JSON_FILE       
   Input file relative or absolute path containing the JSON representation of the gene sequence (a JSON array of the genes as strings).  
      
### Optional:     
- **-m** PATH_TO_SUBSTITUTION_MATRIX_FILE  
  Input file relative or absolute path containing the substitution matrix  
  **Default**: a substitution matrix that does not allow substitutions and rewards a match with a score of 1.  
  
- **-dt** TREE_DELETION_LIMIT  
  The number of allowed deletions from the tree. A non-negative number  
**Default**: 0  
  
- **-ds** STRING_DELETION_LIMIT  
  The number of allowed deletions from the gene sequence. A non-negative number  
  **Defaul**t: 0  
  
- **-o** OUTPUT_OPTIO  
  Possible values: [all, best]  
  **all**: outputs all possible derivations in an descending order according to their score  
  **best**: outputs only the best derivation  
**Default**: best  
  
<a name='input'>Input Formats</a>
--------
### <a name='json_pqt'>Input PQ-tree as a JSON File</a>
Input file containing the JSON representation of the PQ-tree.   
The JSON represantation of a PQ-tree is a JSON object with the key "root" and a JSON object with the **internal node format** or the **leaf format** (described below) as value.
#### Internal Node Format
two key-value pairs:
* Key "type" with one of the following values: "PNode" (if it is a P-node) or "QNode (if it is a Q-node).
* Key "children" that its value is an array of JSON objects with the **internal node format** or the **leaf format** (the array can contain both).
#### Leaf Format
two key-value pairs:
* Key "type" with a string value "Leaf"
* Key "cog" with a string value. This can be any string, and it represents the label of the leaf. It will determine the allowed mappings of the leaf and their score.

The path to this file is given as input with the input option **-j**.
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

The path to this file is given as input with the input option **-gf**.
#### Example
In the example below is the content of the file representing a genome that has 5 genes.
```json
["COG1", "COG2", "COG3", "COG2", "COG5"]
```
### <a name='paren'>Input PQ-tree in the Parenthesis Represantation</a>
In the parenthesis represantation of a PQ-tree every internal node is written as a pair of matching brackets and between them the node's children in the parenthesis representation separated by a single space.  
Square brackets [] represent a Q-node and round brackets () represent a P-node.  
To represent a leaf, write its label.

This represantation is given as input with the input option **-p**.  
To pass this represantain as input to the program it needs to be quoted.
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
This is the same tree as in the JSON represantation example
> ((COG1 COG2 COG3) [COG1 COG4] COG4)
* The same PQ-tree as in the last eample, but quoted, so it can be sent as an argument to the program.
> "((COG1 COG2 COG3) [COG1 COG4] COG4)"

### <a name='genome'>Input Genome as String</a>
In the string represantation of the genome the genes of the genome are written in order, separated by a single space.

This represantation is given as input with the input option **-g**.
To pass this represantain as input to the program it needs to be quoted.
#### Examples
* A String representing a genome that have 5 genes (the same genome as in the JSON represantation of the genome).
> COG1 COG2 COG3 COG2 COG5
* The same genome, but quoted so it can be sent as an argument to the program.
> "COG1 COG2 COG3 COG2 COG5"

### <a name='subs'>Input File Containing the Substitution Matrix</a>
A symmetric matrix, where the first line and column are the labels of the PQ-tree leafs and the genes in the genome arranged in the same order. The labels and genes are separated by a TAB character. The score for a substitution is given as a number, or if the substitution is prohibeted a dot symbol (.) is given.
##### A template of the substitution matrix
In this example S_1_2 represents the score for substitution GENE_1 with GENE_2.
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
<!--
<a name='output'>Output Formats</a>
--------
### TODO
-->
<!--
<a name='examples'>Running Examples</a>
--------
### TODO
-->
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
