package ar.com.qiwi.Quiosco;

import java.math.BigDecimal;

public class MapItems {

	private String name;
	private String description;
	private String style;
	private Integer coords_x;
	private Integer coords_y;
	
	public MapItems(String name, String description, String style, Integer coords_x, Integer coords_y) {
		this.name = name;
		this.description = description;
		this.style = style;
		this.coords_x = coords_x;
		this.coords_y = coords_y;
	}
	
	public Integer getCoordsX() {
		return coords_x;
	}
	
	public Integer getCoordsY() {
		return coords_y;
	}
}
