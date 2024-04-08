
# **Problem:**
- Given a list of text boxes/outlines from an image, implement an algorithm that groups these boxes in logical groups.
- The boxes that are sufficiently close and that make sense to be together will form a group. 
- The coordinates are relative to (0,0), representing the upper left corner.

## **Input:**
a list of boxes
## **Output:**
a list of groups

# **Notes:**
- The boxes contain only the coordinates, without the content, so the grouping can be done only based on coordinates.
- The boxes can be considered to be oriented at zero degrees.
- The order of the boxes is random.
