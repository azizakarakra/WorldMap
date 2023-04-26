package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javafx.animation.PathTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Controller {
	@FXML
	TextArea txtAreaPath;
	@FXML
	TextArea txtAreaDistance;
	@FXML
	ChoiceBox<String> box_src;
	@FXML
	ChoiceBox<String> box_target;
	@FXML
	AnchorPane pane;
	ArrayList<String> list = new ArrayList<>();
	LinkedList<City> route = new LinkedList<>();
	ArrayList<Button> buttons = new ArrayList<>();
	Graph<City> cities = new Graph<>();
	ArrayList<Line> lines = new ArrayList<>();
	Rectangle rectPath = new Rectangle();

	@FXML
	private void initialize() {
		readFile();
		genarateButtons();
		box_src.setItems(FXCollections.observableList(list));
		box_target.setItems(FXCollections.observableList(list));
	}

	public void genarateButtons() {
		for (int i = 0; i < buttons.size(); ++i) {
			final Button b = buttons.get(i);
			b.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					if (box_src.getValue() != null &&
						box_src.getValue().equals(b.getText())) //if the city is the src and it was pressed again
						box_src.setValue(null);
					else if (box_src.getValue() != null) 
						box_target.setValue(b.getText()); // if src filled then set value in target
					else
						box_src.setValue(b.getText()); //if src = null then fill it
				}
			});
		}
	}

	public void addButton(String name, double x, double y) { //function to add button to the fx
		x = getX(x);

		y = getY(y);
		Button b = new Button(name);

		b.setLayoutX(x);
		b.setLayoutY(y);
		buttons.add(b);
		pane.getChildren().add(b);
	}

	public double getX(double x) {
		if (x > 0)
			x = 398 + 2.25 * x;
		else
			x = 398 - 2.25 * Math.abs(x);
		return x - 20;
	}

	public double getY(double y) {
		double result = 0;
		boolean minus = false;
		if (y < 0) {
			minus = true;
			y = Math.abs(y);
		}
		if (y > 0 && y <= 30)
			result = y * 3.5;

		else if (y > 0) {
			result = 30 * 3.5;
			if (y <= 60) {
				y -= 30; //remove line 102 
				result += y * 4.5; //remaining
			} else if (y > 60) {
				result += 30 * 4.5; 
				if (y < 75) {
					y -= 60; //remove line 107
					result += y * 9;
				} else {
					result += 15 * 9;
					y -= 75;
					result += y * 11.57;
				}
			}
		}

		if (minus)
			result = 450 + result;
		else
			result = 450 - result;

		return result - 25; //for shape
	}

	public void readFile() {
		try {
			Scanner input = new Scanner(new File("data.txt")); //to open file
			String reader = input.nextLine();
			int vertices = Integer.parseInt(reader.split(" ")[0]);
			int edges = Integer.parseInt(reader.split(" ")[1]);

			for (int i = 0; i < vertices; ++i) {
				reader = input.nextLine(); //line 2
				String[] tokens = reader.split(" ");
				list.add(tokens[0]); // to fill the comboBox
				cities.addNewVertex(new City(tokens[0], Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])));
				addButton(tokens[0], Double.parseDouble(tokens[1]), Double.parseDouble(tokens[2])); //to add button
			}

			// second part of file
			for (int i = 0; i < edges; ++i) { // line 52
				reader = input.nextLine();
				String[] tokens = reader.split(" ");

				double x1 = 0, y1 = 0, x0 = 0, y0 = 0;

				for (City key : cities.map.keySet())  //pointer on the set
					if (key.name.equals(tokens[1])) { 
						x1 = key.x;
						y1 = key.y;
					} else if (key.name.equals(tokens[0])) {
						x0 = key.x;
						y0 = key.y;
					}

				// √ (x0 - x1)^2 + (y0 - y1)^2
				double cost = Math.sqrt((Math.pow(x0 - x1, 2)) + (Math.pow(y0 - y1, 2)));

				cities.addNewEdge(new City(tokens[0], x0, y0, cost), new City(tokens[1], x1, y1, cost), true);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public LinkedList<City> djikarta(City source, City destination, HashMap<City, List<City>> map) {
		LinkedList<City> done = new LinkedList<>(); //known
		LinkedList<City> edges = new LinkedList<>(); //Q
		for (int i = 0; i < ((LinkedList<City>) map.get(source)).size(); ++i) {
			edges.add(new City(((LinkedList<City>) map.get(source)).get(i))); // initialize the Q
		}

		while (edges.size() > 0) { // here
			City c = getmin(edges); //deQ
			if (c.from == null)
				c.from = source;
			

			done.add(new City(c));
			if (c.equals(destination)) //stop if we read the dst
				break;
			LinkedList<City> list = new LinkedList<>();// list (children) of the min city (c)
			for (int i = 0; i < map.get(c).size(); ++i)
				list.add(new City(map.get(c).get(i)));

			for (int j = 0; j < list.size(); ++j) {
				if (!done.contains(list.get(j))) { // The Node Is Not Open Yet 
					if (edges.contains(list.get(j))) { // THE NODE IS CONNECTED TO THE SOURCE
														// DIRECTLY OR BY OTHER NODE
						double cost = list.get(j).cost + c.cost; // new cost 
						int index = -1;
						for (int k = 0; k < edges.size(); ++k)
							if (edges.get(k).equals(list.get(j))) {
								index = k;
								break;
							}
						if (cost < edges.get(index).cost) { //new < old
							edges.get(index).cost = cost;
							edges.get(index).from = c;
						}
					} 
					else { // THE NODE IS NOT CONNECTED DIRECTLY TO THE SOURCE
						City newCity = list.get(j);
						newCity.from = c;
						newCity.cost = newCity.cost + c.cost;
						edges.add(new City(newCity));
					}

				}
			}
		}
		LinkedList<City> route = new LinkedList<>(); //result
		City c = new City();
		for (int i = 0; i < done.size(); ++i) //to find dist
			if (done.get(i).equals(destination))
				c = done.get(i);

		if (c.name != null) { //if ther is a way to dst
			route.add(c); // ADD THE DESTINATION THEN START GOING UP UNTIL YOU REACH THE SOURCE
			City pointer = c.from;
			do {
				route.addFirst(pointer);
				pointer = pointer.from;
			} while (pointer != null);
		}
		return route;
	}

	//priorityQ
	// FUNCTION TO GET THE CITY WITH THE MINIMUM PATH FROM THE SOURCE
	public City getmin(LinkedList<City> cities) {
		double min = cities.get(0).cost;
		City c = cities.get(0);
		for (int i = 1; i < cities.size(); ++i) {
			if (cities.get(i).cost < min) {
				c = cities.get(i);
				min = cities.get(i).cost;
			}
		}
		cities.remove(c);
		return new City(c);
	}


	@FXML
	public void Fun_Run(ActionEvent event) {
		reset();

		if (box_src.getValue() == null)
			txtAreaPath.setText("Select Source Country");
		else
			txtAreaPath.setText("");
		if (box_target.getValue() == null)
			txtAreaDistance.setText("Select Destination Country");
		else
			txtAreaDistance.setText("");
		if (box_src.getValue() != null && box_target.getValue() != null) {
			if (!box_src.getValue().equals(box_target.getValue()))
			route = djikarta(new City(box_src.getValue()), new City(box_target.getValue()), cities.map);
			if (route.size() > 0) {
				giveColors(box_src.getValue(), box_target.getValue());
				getPath(route);
				txtAreaPath
						.setText(route.toString().substring(1, route.toString().length() - 1).replaceAll(",", " =>"));
				txtAreaDistance.setText(Double.toString(route.get(route.size() - 1).cost * 100) + "km");
				animatePath();
			} else {
				txtAreaPath.setText("NO PATH FOUND");
				txtAreaDistance.setText("??");
			}
		}
	}

	public void reset() {
		for (int i = 0; i < lines.size(); ++i)
			pane.getChildren().remove(lines.get(i)); //remove previous lines 
		pane.getChildren().remove(rectPath); //remove the rec([])
		lines = new ArrayList<>(); // no line in the list
		for (int i = 0; i < buttons.size(); ++i)
			buttons.get(i).setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
		route=new LinkedList<>();
	}

	public void giveColors(String source, String dest) {
		for (int i = 0; i < buttons.size(); ++i)
			if (buttons.get(i).getText().equals(source) || buttons.get(i).getText().equals(dest))
				buttons.get(i).setStyle("-fx-background-color: #ff0000; -fx-text-fill: white;");
	}

	public void animatePath() { //for []
		rectPath = new Rectangle(lines.get(0).getStartX(), lines.get(0).getStartY(), 25, 25);
		rectPath.setArcHeight(5);
		rectPath.setArcWidth(5);
		rectPath.setFill(Color.DEEPPINK);
		rectPath.setOpacity(.65);
		Path path = new Path();
		path.getElements().add(new MoveTo(lines.get(0).getStartX() - 10, lines.get(0).getStartY() - 10));
		for (int i = 0; i < lines.size(); ++i)
			path.getElements().add(new LineTo(lines.get(i).getEndX(), lines.get(i).getEndY()));
		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.seconds(6));
		pathTransition.setPath(path);
		pathTransition.setNode(rectPath);
		pane.getChildren().add(rectPath);
		pathTransition.play();

	}

	public void getPath(LinkedList<City> route) { //for line generating
		for (int i = 0; i + 1 < route.size(); ++i) {
			double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
			for (int j = 0; j < buttons.size(); ++j) {
				if (buttons.get(j).getText().equals(route.get(i).name)) {
					x1 = buttons.get(j).getLayoutX() + 30;
					y1 = buttons.get(j).getLayoutY() + 10;
				} else if (buttons.get(j).getText().equals(route.get(i + 1).name)) {
					x2 = buttons.get(j).getLayoutX() + 30;
					y2 = buttons.get(j).getLayoutY() + 10;
				}
			}
			Line line = new Line(x1, y1, x2, y2);
			line.setStroke(Color.DARKGOLDENROD);
			line.setStrokeWidth(3);
			lines.add(line);
			pane.getChildren().add(line);
		}
	}
}