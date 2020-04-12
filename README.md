# UnorderedParsing

<a name='prerequisites'>Prerequisites</a>
--------

[Java Runtime Environment (JRE)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
8 or higher.

<a name='running'>Running Unordered Parsing</a>
--------

- Download the jar file `UnorderedParsing.jar`
- Run the program:  
  In the terminal (linux) or cmd (windows) type:
```
java -jar .\UnorderedParsing.jar [options]
```

#### Options:
Mandatory:
- An argument for the PQ-tree using one of the following options:
  - **-j** PQT_JSON_FILE_NAME      
  Input file relative or absolute path containing the JSON representation of the tree.   
  - **-p** PARENTHESIS_REPRESENTATION_OF_PQT       
  The PQ-tree in its parenthesis representation  
      
- An argument for the gene sequence using one of the following options:  
  - **-g** GENE_SEQUENSE_AS_STRING      
   A string of the genes separated by a single space  
  - **-gf** GENE_SEQUENCE_JSON_FILE       
   Input file relative or absolute path containing the JSON representation of the gene sequence (a JSON array of the genes as strings).  
      
Optional:     
- **-m** PATH_TO_SUBSTITUTION_MATRIX_FILE  
  Input file relative or absolute path containing the substitution matrix  
  Default: a substitution matrix that does not allow substitutions  
  
- **-dt** TREE_DELETION_LIMIT  
  The number of allowed deletions from the tree. A non-negative number  
  Default: 0  
  
- **-ds** STRING_DELETION_LIMIT  
  The number of allowed deletions from the gene sequence. A non-negative number  
  Default: 0  
  
- **-o** OUTPUT_OPTIO  
  Possible values: [all, best]  
  all: outputs all possible derivations in an descending order according to their score  
  best: outputs only the best derivation  
  Default: best  
  
<a name='input'>Input Formats</a>
--------

