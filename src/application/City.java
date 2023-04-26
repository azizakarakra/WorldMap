package application;

import java.util.ArrayList;
import java.util.Objects;

public class City {

	String name;
	double cost;
	double x;
	double y;
	City from;

	public City(String name) {
		super();
		this.name = name;
	}

	public City(String name, double x, double y) {
		this.name = name;
		this.x = x;
		this.y = y;
	}

	public City(String name, double x, double y, double cost) {
		super();
		this.name = name;
		this.cost = cost;
		this.x = x;
		this.y = y;
	}

	public City(City city) {
		this.name = city.name;
		this.cost = city.cost;
		this.x = city.x;
		this.y = city.y;
		this.from = city.from;
	}

	public City() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		return Objects.equals(name, other.name);
	}

}
