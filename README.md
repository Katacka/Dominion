# Dominion

If for whatever reason the GUI is failing to work, attempt the following solutions as applicable.
Note: These are UNIX solutions as Windows terminal support is awful. Ryan, best of luck.

Pull Failure: <br />
  #Open a terminal  <br />
  git clone https://github.com/Katacka/Dominion/ #This should create a directory containing the git repo, prepend sudo if necessary <br />
  #Open the resulting folder as a new project within Android Stuido <br />
  #Things should work <br />
  
Push Failure: <br />
  #Open a terminal <br />
  git checkout <Your_Name>\_<Push_Description> #We're going to store all of our changes in a particular branch <br />
  git add -A && git commit -m "<Your_Message>" #In case any files have not been committed <br />
  
  git remote add origin https://github.com/Katacka/Dominion/ #Stores a reference to this repo in the alias origin <br />
  git push origin master --force #Actually pushes to the repo. As things stand I'll have to merge any changes, but wew can develop    a better model in the future <br />
  
  Hopefully that worked to some degree or other! <br />
