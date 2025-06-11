# Thymeleaf template static generated blog - (WIP README.md)

## Purpose of project
I created this because I didn't like Gatsby, Jekyll and Hugo as they offered too many complicated features 
and I wanted to make my own smaller version as a fun project.

## Goal of Project
To pass a folder of markdown files and turn it into a static website.

The project has a limited scope and does have a few assumptions but most things can be changed.
KISS principle was a priority but the development of features was quite ad-hoc.

## Technologies
This is a static site generator using the following technologies;

- Java
- Thymeleaf
- flexmark library - to parse the markdown into html
- snakeyaml library - to parse yaml

## Example folder
The folder "mysite" found in the source-code will be used for reference.

## HOWTO
The basic requirements for a static website to be generated is CSS, templates and markdown files.
Although required in the properties.yaml file, a generated site could be made with no css but an empty directory/string may be required.

CSS, Templates and Markdown need to be provided in accordance to the project structure ideally in their own separate folders as shown in the reference.
Assets (such as CSS) and templates must have the directory listed in the properties.yaml file.
Assets will be copied in their entirety to the target directory and currently there is no functionality for compression.

The program requires an index file to be provided at the root of the project and for any sub-directory containing markdown files.

Each markdown file must have the metadata portion filled in with the relevant details such as title, draft, template to use and many more.
Index files are identified through the meta-data.
Metadata is required in each file to function and none are optional.

The contents of the markdown files can link to any asset in the asset folders. Links to other pages may be in HTML format or canonical links.
The result of the program should always be the same.

## Basic General Flow

1. All static assets are immediately copied to the destination asset directory.
2. Initialize the markdown processor
3. Retrieve all the templates
4. Iterate through all files to extract the metadata and html
5. Create a list of entries for the navigation bar made up of all the indexes in the metadata
6. Generate all the non-index pages from the metadata and output them in the destination directory while preserving file structure
7. Generate all index pages
8. Exit

## TODO's - below is the work to be done

In no particular order

1. ~~Index page generates the links to other indexes and aboutme instead of hard coded~~
2. ~~CSS for the entire project~~
3. ~~Metadata~~
4. ~~A way to handle images~~
5. ~~Displaying tags in posts and indexes~~
6. A way to link between posts without hardcoding html - TODO
7. Links to social media not part of a post
8. ~~Favicon~~
9. ~~Blog and Project urls~~
10. ~~Separate into utility classes handling of slugs~~
11. ~~Run the code as a jar file/other format and pass it the files in a structure~~
12. ~~Customizability? Changing meta tags, favicons, css? Most likely will never be implemented~~
13. Css light and dark mode in reference
14. ~~Introduce a base layout~~
15. Proper error handling for failures
16. Unit tests - IMP
17. Guide on hosting on Caddy

# Changelog

- Version 1.0-SNAPSHOT
  - Initial Release



