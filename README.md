# Dominion

## Verbose Project Intialization and Upstream Management

If for whatever reason the GUI is failing to work, attempt the following solutions as applicable.
Note: These are UNIX solutions as Windows terminal support is awful. Ryan, best of luck.

#### Pull Failure: <br />
  #Open a terminal  <br />
  git clone https://github.com/Katacka/Dominion/ #This should create a directory containing the git repo, prepend sudo if necessary <br />
  #Open the resulting folder as a new project within Android Studio <br />
  #Things should work <br />
  
#### Push Failure: <br />
  #Open a terminal <br />
  git checkout <Your_Name>\_<Push_Description> #We're going to store all of our changes in a particular branch <br />
  git add -A && git commit -m "<Your_Message>" #In case any files have not been committed <br />
  
  git remote add origin https://github.com/Katacka/Dominion/ #Stores a reference to this repo in the alias origin <br />
  git push origin master --force #Actually pushes to the repo. As things stand I'll have to merge any changes, but we can develop    a better model in the future <br />
  
  Hopefully that worked to some degree or other! <br />
  
  
## Generic Git Workflow <br />

When starting from scratch, follow all steps as listed below. If you have already intialized a git repo, feel free to skip the first step and instead begin at the point as marked below.<br />

#### Initialization:<br />
  `git clone https://github.com/Katacka/Dominion.git` #Serves to download the repo, setting your remote origin for future pushes<br />

#### Pulling Changes Down from Upstream:<br />
  `git pull` #Origin should be set automatically (having once cloned the proper repo) if errors arise run the following command<br />
  `git remote set-url origin https://github.com/Katacka/Dominion.git` #With any luck, this should reset your origin<br />
  #Re-running `git pull` should now work<br />

#### Checking out a Local Branch:
  `git checkout <Your_Name>_<Push_Description>` #The push description should describe the changes you imagine you'll be making<br />
  #Admittedly, its ultimately arbitrary what you call the branch, so anything conventionally clear will work<br />

#### Saving Changes to Local Branch:<br />
  `git add --all` #Add every file not specified within the project-level relevant .gitignore<br />
  `git commit -m "<Message>"` #Commit all the files just added under the commit description, `<Message>`<br />
  
#### Pushing Changes to Upstream:<br />
  `git push origin <Your_Branch_Name>` #This will push your branch to upstream<br />
  #If for some reason you cannot recall your branch name run `git status`<br />
  #If origin errors, try resetting it with `git remote set-url origin https://github.com/Katacka/Dominion.git`<br />
  
#This following section should not be considered part of a generic workflow, however may prove useful when dealing with merge conflicts. While these really shouldn't arise based on our weekly push philosophy, where we will all sync up, its better to be safe than sorry..?<br />

#### Resolving Merge Conflicts:<br />
  #If you receive a merge error run `git mergetool`<br />
  #Or edit the file by hand to remove the conflict, this is probably more work in most cases<br />
  
