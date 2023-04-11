<img src="src/com/rosty/maze/application/icons/logo_128x128.png" align="right" />

# Mazette
![Available language: English](src/com/rosty/maze/view/pictures/english.png)
![Available language: French](src/com/rosty/maze/view/pictures/french.png)
![Available language: German](src/com/rosty/maze/view/pictures/german.png)
![Available language: Spanish](src/com/rosty/maze/view/pictures/spanish.png)

**Maze generator and solver using different algorithms**

![Version](https://img.shields.io/badge/Version-1.0-blue)
![Language](https://img.shields.io/badge/Language-Java/JavaFX-green)

![Java version](https://img.shields.io/badge/Java%20version-1.8-slategray)
![JavaFX version](https://img.shields.io/badge/JavaFX%20version-8.0-slategray)
![Log4J version](https://img.shields.io/badge/Log4J%20version-1.2.17-slategray)

------

The aim is to display every known algorithm step by step for educational purposes. By the way, the source code aims to be as modular as possible to enlighten the code and re-use some of its components (cf. the abstract class Algorithm).

The software proposes several functionalities:
 * Running algorithms via a command bar; those algorithms can be run **directly** or **step by step**.
 * Customising maze processing (size, duration between two steps, etc.)

![Application main page](res/main_page.png)

## Implemented generation algorithms

All those algorithms are based on the [excellent site from Jamis Buck](http://weblog.jamisbuck.org/2011/2/7/maze-generation-algorithm-recap), doing a very complete state-of-the-art of the mathematical problem, except for the last two algorithms. The former goes around in my head since 2019 (some little ideas went in my head in 2009 when I began studying mazes but I began working seriously on it in 2020) and the latter came to my head in 2021 while studying line rasterization.

- [X] Kruskal (basic algorithm + alternative version with array sort)
- [X] Recursive backtracking
- [X] Recursive division
- [X] Hunt-and-Kill (or HK)
- [X] Eller
- [X] Prim
- [X] Aldous-Broder
- [X] Wilson
- [X] Growing tree
- [X] Binary tree
- [X] Sidewinder
- [X] Personal algorithm (name still to define 😊)
- [X] Personal algorithm #2 (will probably be called the "Mikado algorithm")

## Implemented solving algorithms

The list mainly comes from the [Wikipedia page](https://en.wikipedia.org/wiki/Maze_solving_algorithm). Very interesting content has been found by [SanderJSA](https://github.com/SanderJSA) while looking for Lee's algorithm ([link below](http://cc.ee.ntu.edu.tw/~jhjiang/instruction/courses/spring11-eda/lec06-3_4p.pdf)). Basic implementations have been done for a grid maze but it will be enhanced in another version of the application.

- [X] A* (or A-star)
- [X] Wall following
- [X] Dijkstra
- [X] Prim-Jarnik
- [ ] Pledge
- [ ] Trémeaux
- [ ] Dead-end filling
- [X] Lee (Breadth-First Search)
- [ ] Hadlock
- [ ] Soukup
- [ ] Mikami-Tabushi
- [ ] Hightower
- [X] Random mouse

------

## Features that need to be implemented in the future

 * Saving mazes as an image in many formats (PNG, JPEG, TIFF, SVG, PDF)
 * Color pickers for each maze part
 * Color map for pathfinding algorithms, which clarifies the target from start point to the finish.

------

Documentation from the [Astrolog website](https://astrolog.org/labyrnth/algrithm.htm) provides an interesting overview of the problem. The last solving algorithm comes from this link.
