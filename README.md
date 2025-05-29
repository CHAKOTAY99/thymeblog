# Thymeleaf Generated Blog

This is a static site generator using thymeleaf with the flexmark library to process simple markdown files.

Markdown files are widely supported for my use case.

I created this because I didn't like Gatsby, Jekyll or Hugo as they offered too many features and I wanted to make my own smaller version.

This project is customized to my personal use case and will most likely require code changes to modify css, favicons, etc..

## TODO's - below is the work to be done

In no particular order

1. Index page generates the links to other indexes and aboutme instead of hard coded. - TODO
2. ~~CSS for the entire project~~
3. ~~Metadata~~
4. ~~A way to handle images~~
5. ~~Displaying tags in posts and indexes~~
6. A way to link between posts without hardcoding html - TODO
7. Links to social media
8. ~~Favicon~~
9. ~~Blog and Project urls~~
10. ~~Separate into utility classes handling of slugs~~
11. Run the code as a jar file/other format and pass it the files in a structure - see standalone structure - LARGE TODO // most likely to be done
12. Customizability? Changing meta tags, favicons, css? Most likely will never be implemented
13. Css light and dark mode
14. Introduce a base layout


## Standalone system

In a perfect world I would want to point the jar file to a folder with the entire sitemap and it will output the entire website in an output folder.
YAML will include the template to be used.
YAML could also include the filepath otherwise follow a folder structure

How the nav will be calculated in the desired order is still WIP

Each folder would have it's own index and that would be where the nav would access it

About-me and CV would be two indexes in their own empty folders
