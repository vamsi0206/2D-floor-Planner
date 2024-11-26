import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.Color;

// Enum for Room Types
// Defines different types of rooms with descriptive names
enum RoomType {
    BEDROOM, BATHROOM, KITCHEN, LIVINGROOM
}

// Room Class
// Represents a rectangular room on the canvas
class Room implements Serializable {

    int x, y, width, height;  // Position and dimensions of the room
    RoomType type;            // Type of the room (e.g., Bedroom, Kitchen)
    Color color;              // Color representing the room type
    static final int wallThickness = 2; // Default wall thickness for room boundaries

    public Room(int x, int y, int width, int height, RoomType type) {
        // Initialize room dimensions and type
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        this.color = getColorForType(type);  // Assign a specific color based on the room type
    }

    // Maps each room type to a specific color for visual representation
    private Color getColorForType(RoomType type) {
        return switch (type) {
            case BEDROOM -> Color.GREEN;
            case BATHROOM -> Color.BLUE;
            case KITCHEN -> Color.RED;
            case LIVINGROOM -> Color.ORANGE;
        };
    }

    // Calculates the room's bounding rectangle, including wall thickness
    public Rectangle getBounds() {
        return new Rectangle(x - wallThickness, y - wallThickness,
                width + 2 * wallThickness, height + 2 * wallThickness);
    }

    // Checks if this room's bounds overlap with another rectangle
    public boolean overlaps(Rectangle other) {
        return getBounds().intersects(other);
    }

    // Ensures the room stays within the canvas boundaries
    // Adjusts position to avoid overlaps with existing rooms
    public void keepWithinBounds(int canvasWidth, int canvasHeight, ArrayList<Room> existingRooms) {
        x = Math.max(wallThickness, Math.min(x, canvasWidth - width - wallThickness));
        y = Math.max(wallThickness, Math.min(y, canvasHeight - height - wallThickness));
        for (Room existingRoom : existingRooms) {
            if (existingRoom != this && existingRoom.overlaps(getBounds())) {
                // Push the room back inside the canvas bounds if overlap occurs
                x = Math.max(Room.wallThickness, Math.min(x, canvasWidth - width - Room.wallThickness));
                y = Math.max(Room.wallThickness, Math.min(y, canvasHeight - height - Room.wallThickness));
            }
        }
    }
}

// Furniture Class
// Represents a piece of furniture with rotation, resizing, and bounds handling
class Furniture implements Serializable {

    int x, y, width, height;     // Position and dimensions of the furniture
    String type;                 // Type of furniture (e.g., Sofa, Table)
    ImageIcon icon;              // Graphical representation of the furniture
    String iconPath;             // Path to the furniture's icon
    private double angle = 0;    // Rotation angle in radians
    private static final int RESIZE_HANDLE_SIZE = 8;  // Size of the resize handle in pixels

    public Furniture(int x, int y, int width, int height, String type) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        // Map furniture type to icon path
        this.iconPath = switch (type) {
            case "Sofa" -> "/sofa.png";
            case "Table" -> "/table.png";
            case "Chair" -> "/chair.png";
            case "Bed" -> "/bed.png";
            case "Dining_Set" -> "/diningset.png";
            case "Door" -> "/door.png";
            case "Window" -> "/window.png";
            case "Stove" -> "/stove.png";
            case "Shower" -> "/shower.png";
            case "Commode" -> "/commode.png";
            case "Wash_Basin" -> "/washbasin.png";
            case "Sink" -> "/sink.png";
            default -> "/default.png";
        };

        // Load and scale the icon
        this.icon = new ImageIcon(getClass().getResource(iconPath));
        Image img = icon.getImage();
        Image scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
    }

    // Returns the bounding rectangle of the furniture
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Adjusts the dimensions of the furniture for resizing
    public void resize(int dx, int dy) {
        this.width += dx;
        this.height += dy;
    }

    // Checks if a point is near the resize handle
    public boolean isNearResizeHandle(Point point) {
        return (point.x >= x + width - RESIZE_HANDLE_SIZE && point.x <= x + width &&
                point.y >= y + height - RESIZE_HANDLE_SIZE && point.y <= y + height);
    }

    // Draws resize handles (small squares) on the furniture
    public void drawResizeHandles(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(x + width - RESIZE_HANDLE_SIZE, y + height - RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE, RESIZE_HANDLE_SIZE);
    }

    // Ensures the furniture stays within the canvas boundaries
    public void keepWithinBounds(int canvasWidth, int canvasHeight) {
        x = Math.max(0, Math.min(x, canvasWidth - width));
        y = Math.max(0, Math.min(y, canvasHeight - height));
    }

    // Draws the furniture, applying rotation and scaling
    public void draw(Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform(); // Save original transform
        g2d.translate(x + width / 2.0, y + height / 2.0);       // Move to the center
        g2d.rotate(angle);                                     // Apply rotation
        g2d.translate(-width / 2.0, -height / 2.0);            // Move back to top-left corner
        g2d.drawImage(icon.getImage(), 0, 0, width, height, null); // Draw furniture
        g2d.setTransform(originalTransform);                   // Restore transform
        drawResizeHandles(g2d);                                // Draw resize handles
    }

    // Rotates the furniture by 90 degrees and adjusts its dimensions
    public void rotate() {
        int temp = width;
        width = height;
        height = temp;
        angle += Math.toRadians(90);
        angle %= 2 * Math.PI; // Keep angle within 0-2π
    }
}

// Door class
// Specialized furniture representing a door
class Door extends Furniture {
    public Door(int x, int y, int width, int height) {
        super(x, y, width, height, "Door");
    }

    @Override
    public void keepWithinBounds(int canvasWidth, int canvasHeight) {
        super.keepWithinBounds(canvasWidth, canvasHeight); // Custom logic for doors can be added here
    }
}

// Window class
// Specialized furniture representing a window
class Window extends Furniture {
    public Window(int x, int y, int width, int height) {
        super(x, y, width, height, "Window");
    }

    @Override
    public void keepWithinBounds(int canvasWidth, int canvasHeight) {
        super.keepWithinBounds(canvasWidth, canvasHeight); // Custom logic for windows can be added here
    }
}

// Canvas Panel
class CanvasPanel extends JPanel {
    private ArrayList<Room> rooms = new ArrayList<>(); // Stores all rooms on the canvas
    private ArrayList<Furniture> furnitureList = new ArrayList<>(); // Stores all furniture
    private Room selectedRoom = null; // Tracks the currently selected room
    private Furniture selectedFurniture = null; // Tracks the currently selected furniture
    private Point initialClick; // Stores the initial mouse click position for dragging
    private Furniture resizingFurniture = null; // Tracks the furniture being resized
    private Point resizingInitialClick; // Stores the initial mouse click for resizing
    private boolean isResizing = false; // Tracks if resizing is currently happening

    // Constants for room and canvas properties
    private static final int ROOM_SPACING = 20; // Space between rooms horizontally
    private static final int ROOM_HEIGHT = 100; // Height of each room
    private static final int CANVAS_WIDTH = 900; // Canvas width
    private static final int CANVAS_HEIGHT = 600; // Canvas height
    private static final int MAX_ROOMS_IN_ROW = 1000; // Maximum number of rooms in a row
    public static final int GRID_SIZE = 20; // Grid size for snapping

    public CanvasPanel() {
        // Set background and canvas size
        setBackground(Color.LIGHT_GRAY);
        setPreferredSize(new Dimension(CANVAS_WIDTH / 4, CANVAS_HEIGHT));

        // Mouse listeners for interactions with rooms and furniture
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Check if resizing handle of any furniture is clicked
                for (Furniture furniture : furnitureList) {
                    if (furniture.isNearResizeHandle(e.getPoint())) {
                        resizingFurniture = furniture;
                        resizingInitialClick = e.getPoint();
                        isResizing = true;
                        return; // Exit since resizing is prioritized
                    }
                }
                // Select a room or furniture based on click position
                selectRoom(e.getPoint());
                selectFurniture(e.getPoint());
                initialClick = e.getPoint(); // Record initial click for dragging
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isResizing = false; // Reset resizing flag
                if (resizingFurniture != null) {
                    resizingFurniture.keepWithinBounds(getWidth(), getHeight());
                    repaint(); // Redraw the canvas
                }
                if (selectedRoom != null) {
                    selectedRoom.keepWithinBounds(getWidth(), getHeight(), rooms);
                    repaint();
                }
                if (selectedFurniture != null) {
                    selectedFurniture.keepWithinBounds(getWidth(), getHeight());
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Handle resizing of furniture
                if (isResizing && resizingFurniture != null) {
                    int dx = e.getX() - resizingInitialClick.x; // Change in x
                    int dy = e.getY() - resizingInitialClick.y; // Change in y
                    resizingFurniture.resize(dx, dy); // Adjust size
                    resizingInitialClick = e.getPoint(); // Update reference point
                    repaint();
                    return; // Exit since resizing takes precedence
                }

                // Handle dragging of furniture within the canvas or rooms
                if (selectedFurniture != null && selectedRoom != null) {
                    int dx = e.getX() - initialClick.x;
                    int dy = e.getY() - initialClick.y;
                    selectedFurniture.x += dx;
                    selectedFurniture.y += dy;

                    moveFurnitureInsideRoom(selectedFurniture, selectedRoom);
                    initialClick = e.getPoint(); // Update drag reference
                    repaint();
                }

                // Handle room dragging
                if (selectedRoom != null) {
                    int dx = e.getX() - initialClick.x;
                    int dy = e.getY() - initialClick.y;
                    selectedRoom.x += dx;
                    selectedRoom.y += dy;
                    initialClick = e.getPoint();

                    boolean overlapDetected = false;
                    for (Room room : rooms) {
                        if (room != selectedRoom && room.overlaps(selectedRoom.getBounds())) {
                            overlapDetected = true;
                            break;
                        }
                    }

                    if (overlapDetected) {
                        selectedRoom.x -= dx; // Revert position
                        selectedRoom.y -= dy;
                        JOptionPane.showMessageDialog(CanvasPanel.this, "Cannot move the room: Overlap detected!");
                    } else {
                        selectedRoom.keepWithinBounds(getWidth(), getHeight(), rooms);
                        repaint();
                    }
                }

                // Handle free dragging of furniture on the canvas
                if (selectedFurniture != null) {
                    int dx = e.getX() - initialClick.x;
                    int dy = e.getY() - initialClick.y;
                    selectedFurniture.x += dx;
                    selectedFurniture.y += dy;
                    initialClick = e.getPoint();

                    boolean overlapDetected = false;
                    for (Furniture furniture : furnitureList) {
                        if (furniture != selectedFurniture && furniture.getBounds().intersects(selectedFurniture.getBounds())) {
                            overlapDetected = true;
                            break;
                        }
                    }

                    if (overlapDetected) {
                        selectedFurniture.x -= dx; // Revert position
                        selectedFurniture.y -= dy;
                        JOptionPane.showMessageDialog(CanvasPanel.this, "No overlap between furniture!");
                    }
                    repaint();
                }
            }
        });
    }

    // Restrict furniture movement within the room
    public void moveFurnitureInsideRoom(Furniture furniture, Room room) {
        int maxX = room.x + room.width - furniture.width;
        int maxY = room.y + room.height - furniture.height;

        furniture.x = Math.max(room.x, Math.min(furniture.x, maxX));
        furniture.y = Math.max(room.y, Math.min(furniture.y, maxY));
    }

    // Rotation for selected furniture
    public void rotateSelectedFurniture() {
        if (selectedFurniture != null) {
            selectedFurniture.rotate();
            selectedFurniture.keepWithinBounds(getWidth(), getHeight());
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No furniture selected to rotate.");
        }
    }

    // Add a room with overlap checks
    public void addRoom(Room room) {
        room.keepWithinBounds(getWidth(), getHeight(), rooms);
        for (Room existingRoom : rooms) {
            if (existingRoom.overlaps(room.getBounds())) {
                JOptionPane.showMessageDialog(this, "Rooms cannot overlap!");
                return;
            }
        }
        rooms.add(room);
        repaint();
    }

    // Add furniture with overlap checks
    public void addFurniture(Furniture furniture) {
        for (Furniture existingFurniture : furnitureList) {
            if (existingFurniture.getBounds().intersects(furniture.getBounds())) {
                JOptionPane.showMessageDialog(this, "Furniture cannot overlap!");
                return;
            }
        }
        furniture.keepWithinBounds(getWidth(), getHeight());
        furnitureList.add(furniture);
        repaint();
    }

    // Delete the selected room
    public void deleteSelectedRoom() {
        if (selectedRoom != null) {
            rooms.remove(selectedRoom);
            selectedRoom = null;
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No room selected to delete.");
        }
    }

    // Delete the selected furniture
    public void deleteSelectedFurniture() {
        if (selectedFurniture != null) {
            furnitureList.remove(selectedFurniture);
            selectedFurniture = null;
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "No furniture selected to delete.");
        }
    }

    // Save the plan to a file
    public void savePlan(File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(rooms);
            oos.writeObject(furnitureList);
            JOptionPane.showMessageDialog(this, "Plan saved successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to save the plan.");
        }
    }

    // Load the plan from a file
    @SuppressWarnings("unchecked")
    public void loadPlan(File file) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            rooms = (ArrayList<Room>) ois.readObject();
            furnitureList = (ArrayList<Furniture>) ois.readObject();
            repaint();
            JOptionPane.showMessageDialog(this, "Plan loaded successfully!");
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Failed to load the plan.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        // Draw rooms with walls
        for (Room room : rooms) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(room.x - Room.wallThickness, room.y - Room.wallThickness,
                    room.width + 2 * Room.wallThickness, room.height + 2 * Room.wallThickness);

            g.setColor(room.color);
            g.fillRect(room.x, room.y, room.width, room.height);

            g.setColor(Color.BLACK);
            g.drawRect(room.x, room.y, room.width, room.height);
        }

        // Draw furniture
        for (Furniture furniture : furnitureList) {
            furniture.draw(g2d);
        }
    }

    // Select a room at a given point
    private void selectRoom(Point point) {
        selectedRoom = null;
        for (Room room : rooms) {
            if (room.getBounds().contains(point)) {
                selectedRoom = room;
                return;
            }
        }
    }

    // Select furniture at a given point
    private void selectFurniture(Point point) {
        selectedFurniture = null;
        for (Furniture furniture : furnitureList) {
            if (furniture.getBounds().contains(point)) {
                selectedFurniture = furniture;
                return;
            }
        }
    }
}

// Control Panel
// ControlPanel Class: This panel allows users to interact with the floor plan and modify its components.
class ControlPanel extends JPanel {
    private CanvasPanel canvas;

    // Constructor for ControlPanel that initializes the layout and components.
    public ControlPanel(CanvasPanel canvas) {
        this.canvas = canvas;  // Store the reference to the CanvasPanel
        setLayout(new GridLayout(0, 2, 5, 5));  // Grid layout for the control panel

        // Width input field and label
        JLabel widthLabel = new JLabel("Width:");
        JTextField widthField = new JTextField(6);

        // Height input field and label
        JLabel heightLabel = new JLabel("Height:");
        JTextField heightField = new JTextField(6);

        // Combo box for selecting room type (using RoomType enum)
        JComboBox<RoomType> roomTypeCombo = new JComboBox<>(RoomType.values());

        // Button for adding a new room
        JButton addRoomButton = new JButton("Add Room");
        addRoomButton.addActionListener(e -> {
            try {
                // Parse width and height, and create a new Room object
                int width = Integer.parseInt(widthField.getText());
                int height = Integer.parseInt(heightField.getText());
                RoomType type = (RoomType) roomTypeCombo.getSelectedItem();
                Room room = new Room(10, 10, width, height, type);
                canvas.addRoom(room);  // Add room to canvas
            } catch (NumberFormatException ex) {
                // Show error message if invalid dimensions are entered
                JOptionPane.showMessageDialog(this, "Enter valid dimensions.");
            }
        });

        // Button for deleting a selected room
        JButton deleteRoomButton = new JButton("Delete Room");
        deleteRoomButton.addActionListener(e -> canvas.deleteSelectedRoom());

        // Label and combo box for selecting furniture/fixtures to add
        JLabel furnitureLabel = new JLabel("Furniture/Fixture Options:");
        JComboBox<String> furnitureCombo = new JComboBox<>(new String[]{
            "Sofa", "Table", "Chair", "Bed", "Dining_Set", "Door", "Window", "Commode", "Wash_Basin", "Shower", "Sink", "Stove"
        });

        // Button for adding selected furniture/fixture
        JButton addFurnitureButton = new JButton("Add Furniture/Fixture");
        addFurnitureButton.addActionListener(e -> {
            String selectedFurniture = (String) furnitureCombo.getSelectedItem();
            Furniture furniture;
            String iconPath = "";  // Declare iconPath to store the image path

            // Set iconPath based on selected furniture type
            switch (selectedFurniture) {
                case "Sofa" -> iconPath = "/sofa.png";
                case "Table" -> iconPath = "/table.png";
                case "Chair" -> iconPath = "/chair.png";
                case "Bed" -> iconPath = "/bed.png";
                case "Dining_Set" -> iconPath = "/diningset.png";
                case "Door" -> iconPath = "/door.png";
                case "Window" -> iconPath = "/window.png";
                case "Stove" -> iconPath = "/stove.png";
                case "Shower" -> iconPath = "/shower.png";
                case "Commode" -> iconPath = "/commode.png";
                case "Wash_Basin" -> iconPath = "/washbasin.png";
                case "Sink" -> iconPath = "/sink.png";
                default -> iconPath = "/default.png";  // Default icon if furniture is unknown
            }

            // Create specific furniture objects for "Door" and "Window" with unique dimensions
            if ("Door".equals(selectedFurniture)) {
                furniture = new Door(50, 50, 50, 20);  // Example dimensions for door
            } else if ("Window".equals(selectedFurniture)) {
                furniture = new Window(100, 50, 60, 20);  // Example dimensions for window
            } else {
                // Default furniture for other types
                furniture = new Furniture(50, 50, 50, 30, selectedFurniture);
            }

            furniture.iconPath = iconPath;  // Set the icon path for the furniture
            canvas.addFurniture(furniture);  // Add furniture to canvas
        });

        // Button for deleting selected furniture
        JButton deleteFurnitureButton = new JButton("Delete Furniture/Fixture");
        deleteFurnitureButton.addActionListener(e -> canvas.deleteSelectedFurniture());

        // Button for saving the floor plan to a file
        JButton savePlanButton = new JButton("Save Plan");
        savePlanButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                // Save the plan to the selected file
                canvas.savePlan(fileChooser.getSelectedFile());
            }
        });

        // Button for loading a floor plan from a file
        JButton loadPlanButton = new JButton("Load Plan");
        loadPlanButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                // Load the plan from the selected file
                canvas.loadPlan(fileChooser.getSelectedFile());
            }
        });

        // Button for rotating the selected furniture
        JButton rotateButton = new JButton("Rotate Furniture");
        rotateButton.addActionListener(e -> canvas.rotateSelectedFurniture());

        // Adding components to the control panel
        add(widthLabel);
        add(widthField);
        add(heightLabel);
        add(heightField);
        add(new JLabel("Room Type:"));
        add(roomTypeCombo);
        add(addRoomButton);
        add(deleteRoomButton);
        add(new JLabel("Furniture/Fixture:"));
        add(furnitureCombo);
        add(addFurnitureButton);
        add(deleteFurnitureButton);  // Add delete furniture button
        add(savePlanButton);
        add(loadPlanButton);
        add(rotateButton);  // Add rotate button
    }
}

// Main Frame: This class sets up the JFrame with the control panel and canvas.
public class FloorPlanner {
    public static void main(String[] args) {
        // Create the main frame for the floor planner
        JFrame frame = new JFrame("2D Floor Planner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);  // Set window size
        CanvasPanel canvas = new CanvasPanel();  // Create canvas for drawing
        ControlPanel controls = new ControlPanel(canvas);  // Create control panelṇ
        frame.setLayout(new BorderLayout());  // Use border layout for placing components
        frame.add(controls, BorderLayout.WEST);  // Add control panel to the left
        frame.add(canvas, BorderLayout.CENTER);  // Add canvas to the center
        frame.setVisible(true);  // Make the frame visible
    }
}
