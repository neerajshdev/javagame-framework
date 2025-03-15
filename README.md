# Java Game Framework - Learning Project

A learning-focused framework for understanding 2D game development concepts in Java. This project provides essential functionality examples for educational purposes including graphics rendering, input handling, sound processing, resource management, and more.

## Project Overview

This project is **not a complete game framework** but rather a Java-based educational tool designed to help learners understand the fundamentals of creating 2D games. It's freely available for learning purposes and demonstrates several key game development components:

- **Game Loop Management**: Examples of efficient game loops with timing control
- **Input Handling**: Keyboard and mouse input management demonstrations
- **Graphics Rendering**: Basic 2D rendering capabilities for learning
- **Window Management**: Fullscreen and windowed mode support examples
- **Vector & Matrix Math**: 2D vector and matrix operations for understanding game physics and transformations
- **Resource Loading**: Examples of managing game assets like images and sounds
- **Sound System**: Basic audio playback functionality for educational purposes

## Educational Purpose

This framework is designed as a learning resource that can help you:

- Understand core game development concepts
- Learn how game loops and timing work
- Practice implementing input handling in games
- Explore 2D graphics rendering in Java
- Experiment with different game components in a structured environment

The code is freely available for anyone interested in learning game development. You are encouraged to:
- Study the examples
- Modify the code to see how changes affect behavior
- Build your own experimental games using the framework as a foundation
- Use it as a reference for understanding game development patterns

## Project Structure

```
├── src/
│   └── javagames/
│       ├── example/     # Example implementations organized by chapters
│       │   ├── ch1/     # Basic window and display mode examples
│       │   ├── ch2/     # Input handling examples
│       │   └── ...      # More advanced examples in subsequent chapters
│       ├── filesandres/ # File and resource handling utilities
│       ├── sound/       # Sound processing and playback utilities
│       └── util/        # Core utilities for the framework
│           ├── GameFramework.java      # Main abstract game class
│           ├── Vector2f.java           # 2D vector implementation
│           ├── Matrix3x3f.java         # 3x3 matrix for 2D transformations
│           ├── KeyboardInput.java      # Keyboard input handler
│           ├── SimpleMouseInput.java   # Basic mouse input handler
│           ├── RelativeMouseInput.java # Advanced mouse input handler
│           └── ...                     # Other utility classes
├── res/
│   └── assets/          # Game assets (images, sounds, etc.)
│       └── sound/       # Sound files for the examples
└── JGAME.iml            # IntelliJ IDEA project file
```

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- An IDE such as IntelliJ IDEA (recommended) or Eclipse

### Running the Examples

1. Clone this repository
2. Open the project in your IDE
3. Navigate to any example class in the `src/javagames/example/` directory
4. Run the main method of the example

## Creating Your Own Game

To create a game using this framework:

1. Create a new class that extends `GameFramework`
2. Implement the required abstract methods:
   - `createFramework()`: Initialize your game components
   - `renderFrame(Graphics g)`: Draw your game objects
   - `getScreenWidth()`: Define your window width
   - `getScreenHeight()`: Define your window height
3. Override other methods as needed:
   - `initialize()`: Set up your game state
   - `processInput(float dt)`: Handle user input
   - `updateObjects(float dt)`: Update game logic and physics
   - `render(Graphics g)`: Render game objects

## Example

Here's a simple example of a game class structure:

```java
public class MyGame extends GameFramework {
    
    public MyGame() {
        appTitle = "My Awesome Game";
        appWidth = 800;
        appHeight = 600;
    }
    
    @Override
    protected void createFramework() {
        // Initialize framework components
        canvas = new Canvas();
        canvas.setSize(appWidth, appHeight);
        canvas.setBackground(appBackground);
        canvas.setIgnoreRepaint(true);
        getContentPane().add(canvas);
        setLocationByPlatform(true);
        
        if (appCursorDisable) {
            disableCursor();
        }
        
        // Create buffer strategy
        canvas.createBufferStrategy(2);
        bs = canvas.getBufferStrategy();
        
        // Setup input
        setupInput(canvas);
        
        // Initialize frame rate counter
        frameRate = new FrameRate();
        frameRate.initialize();
    }
    
    @Override
    protected void renderFrame(Graphics g) {
        // Clear the back buffer
        g.clearRect(0, 0, getScreenWidth(), getScreenHeight());
        
        // Draw game elements
        // ...
        
        // Display frame rate
        frameRate.calculate();
        g.setColor(appFPSColor);
        g.setFont(appFPSFont);
        frameRate.draw(g, 10, 20);
    }
    
    @Override
    protected int getScreenWidth() {
        return appWidth;
    }
    
    @Override
    protected int getScreenHeight() {
        return appHeight;
    }
    
    // Launch the game
    public static void main(String[] args) {
        launchApp(new MyGame());
    }
}
```

## Learning Path

The examples in the `src/javagames/example/` directory are organized by chapters, likely following a book or tutorial series. They progress from basic concepts to more advanced game development techniques:

- Chapter 1: Basic window management and display modes
- Chapter 2: Input handling (keyboard and mouse)
- Subsequent chapters: More advanced game development topics

## License

This project is freely available for educational and learning purposes. You may:
- Use the code for personal learning
- Modify and experiment with the code
- Use it as a reference for your own educational projects

This is not intended for commercial use or as a production-ready game engine.

## Acknowledgments

This learning project is designed to help beginners understand game development concepts in Java. It may be based on or inspired by various game development tutorials and educational resources. 