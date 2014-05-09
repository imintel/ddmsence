/* Copyright 2010 - 2013 by Brian Uri!
   
   This file is part of DDMSence.
   
   This library is free software; you can redistribute it and/or modify
   it under the terms of version 3.0 of the GNU Lesser General Public 
   License as published by the Free Software Foundation.
   
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
   GNU Lesser General Public License for more details.
   
   You should have received a copy of the GNU Lesser General Public 
   License along with DDMSence. If not, see <http://www.gnu.org/licenses/>.

   You can contact the author at ddmsence@urizone.net. The DDMSence
   home page is located at http://ddmsence.urizone.net/
 */
package buri.ddmsence.ddms.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nu.xom.Element;
import nu.xom.Elements;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.ITspiShape;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.summary.gml.Point;
import buri.ddmsence.ddms.summary.gml.Polygon;
import buri.ddmsence.ddms.summary.tspi.Circle;
import buri.ddmsence.ddms.summary.tspi.Ellipse;
import buri.ddmsence.ddms.summary.tspi.Envelope;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:boundingGeometry.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 *  {@table.header History}
 *  	<p>The GML shapes were replaced by TSPI shapes in DDMS 5.0.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info gml:Polygon|0..1|11110}
 * 		{@child.info gml:Point|0..1|11110}
 * 		{@child.info tspi:Polygon|0..1|00001} 
 * 		{@child.info tspi:Point|0..1|00001}
 * 		{@child.info tspi:Envelope|0..1|00001}
 *  	{@child.info tspi:Circle|0..1|00001}
 *   	{@child.info tspi:Ellipse|0..1|00001}
 * {@table.footer}
 * {@table.header Attributes}
 * 		None.
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule At least 1 shape must exist.|Error|11111}
 * 		{@ddms.rule TSPI shapes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule GML shapes are not used after the DDMS version in which they were removed.|Error|11111}
 * {@table.footer}
 *  
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class BoundingGeometry extends AbstractBaseComponent {

	private List<Polygon> _polygons = null;
	private List<Point> _points = null;
	private List<ITspiShape> _tspiShapes = null;

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public BoundingGeometry(Element element) throws InvalidDDMSException {
		try {
			Util.requireDDMSValue("boundingGeometry element", element);
			setXOMElement(element, false);
			if (!getDDMSVersion().isAtLeast("5.0")) {
				String gmlNamespace = getDDMSVersion().getGmlNamespace();
				_polygons = new ArrayList<Polygon>();
				_points = new ArrayList<Point>();
				Elements polygons = element.getChildElements(Polygon.getName(getDDMSVersion()), gmlNamespace);
				for (int i = 0; i < polygons.size(); i++) {
					_polygons.add(new Polygon(polygons.get(i)));
				}
				Elements points = element.getChildElements(Point.getName(getDDMSVersion()), gmlNamespace);
				for (int i = 0; i < points.size(); i++) {
					_points.add(new Point(points.get(i)));
				}
			}
			else {
				String tspiNamespace = getDDMSVersion().getTspiNamespace();
				_tspiShapes = new ArrayList<ITspiShape>();
				Elements circles = element.getChildElements(Circle.getName(getDDMSVersion()), tspiNamespace);
				for (int i = 0; i < circles.size(); i++) {
					_tspiShapes.add(new Circle(circles.get(i)));
				}
				Elements ellipses = element.getChildElements(Ellipse.getName(getDDMSVersion()), tspiNamespace);
				for (int i = 0; i < ellipses.size(); i++) {
					_tspiShapes.add(new Ellipse(ellipses.get(i)));
				}
				Elements envelopes = element.getChildElements(Envelope.getName(getDDMSVersion()), tspiNamespace);
				for (int i = 0; i < envelopes.size(); i++) {
					_tspiShapes.add(new Envelope(envelopes.get(i)));
				}
				Elements points = element.getChildElements(
					buri.ddmsence.ddms.summary.tspi.Point.getName(getDDMSVersion()), tspiNamespace);
				for (int i = 0; i < points.size(); i++) {
					_tspiShapes.add(new buri.ddmsence.ddms.summary.tspi.Point(points.get(i)));
				}
				Elements polygons = element.getChildElements(
					buri.ddmsence.ddms.summary.tspi.Polygon.getName(getDDMSVersion()), tspiNamespace);
				for (int i = 0; i < polygons.size(); i++) {
					_tspiShapes.add(new buri.ddmsence.ddms.summary.tspi.Polygon(polygons.get(i)));
				}
			}
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data, in earlier versions of DDMS.
	 * 
	 * @param polygons an ordered list of the GML polygons used in this geometry
	 * @param points an ordered list of the GML points used in this geometry
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public BoundingGeometry(List<Polygon> polygons, List<Point> points) throws InvalidDDMSException {
		try {
			if (polygons == null)
				polygons = Collections.emptyList();
			if (points == null)
				points = Collections.emptyList();
			Element element = Util.buildDDMSElement(BoundingGeometry.getName(DDMSVersion.getCurrentVersion()), null);
			for (Polygon polygon : polygons)
				element.appendChild(polygon.getXOMElementCopy());
			for (Point point : points)
				element.appendChild(point.getXOMElementCopy());
			_polygons = polygons;
			_points = points;
			setXOMElement(element, true);
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param shapes a list of TSPI shapes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public BoundingGeometry(List<ITspiShape> shapes) throws InvalidDDMSException {
		try {
			Element element = Util.buildDDMSElement(BoundingGeometry.getName(DDMSVersion.getCurrentVersion()), null);
			if (shapes == null)
				shapes = Collections.emptyList();
			for (ITspiShape shape : shapes)
				element.appendChild(shape.getXOMElementCopy());
			_tspiShapes = shapes;
			setXOMElement(element, true);
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}
	
	/**
	 * @see AbstractBaseComponent#validate()
	 */
	protected void validate() throws InvalidDDMSException {
		Util.requireDDMSQName(getXOMElement(), BoundingGeometry.getName(getDDMSVersion()));
		if (!getDDMSVersion().isAtLeast("5.0")) {
			if (getGmlPolygons().size() + getGmlPoints().size() == 0) {
				throw new InvalidDDMSException("At least 1 of Polygon or Point must be used.");
			}
		}
		else {
			if (getGmlPolygons().size() + getGmlPoints().size() > 0) {
				throw new InvalidDDMSException("boundingGeometry must be defined with TSPI shapes.");
			}
			if (getTspiShapes().isEmpty())
				throw new InvalidDDMSException("At least 1 TSPI shape must exist.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		StringBuffer text = new StringBuffer();
		if (!getDDMSVersion().isAtLeast("5.0")) {
			text.append(buildOutput(isHTML, localPrefix, getGmlPolygons()));
			text.append(buildOutput(isHTML, localPrefix, getGmlPoints()));
		}
		else {
			for (ITspiShape shape : getTspiShapes())
				text.append(shape.getOutput(isHTML, localPrefix, ""));
		}
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getGmlPoints());
		list.addAll(getGmlPolygons());
		list.addAll(getTspiShapes());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof BoundingGeometry))
			return (false);
		return (true);
	}

	/**
	 * Accessor for the element name of this component, based on the version of DDMS used
	 * 
	 * @param version the DDMSVersion
	 * @return an element name
	 */
	public static String getName(DDMSVersion version) {
		Util.requireValue("version", version);
		return ("boundingGeometry");
	}

	/**
	 * Accessor for the GML polygons in this geometry. 
	 */
	public List<Polygon> getGmlPolygons() {
		if (_polygons == null)
			_polygons = Collections.emptyList();
		return (Collections.unmodifiableList(_polygons));
	}

	/**
	 * Accessor for the GML points in this geometry.
	 */
	public List<Point> getGmlPoints() {
		if (_points == null)
			_points = Collections.emptyList();
		return (Collections.unmodifiableList(_points));
	}

	/**
	 * Accessor for the TSPI shapes in this geometry.
	 */
	public List<ITspiShape> getTspiShapes() {
		if (_tspiShapes == null)
			_tspiShapes = Collections.emptyList();
		return (Collections.unmodifiableList(_tspiShapes));
	}
	
	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -5734267242408462644L;
		private List<Polygon.Builder> _gmlPolygons;
		private List<Point.Builder> _gmlPoints;
		private List<Circle.Builder> _circles;
		private List<Ellipse.Builder> _ellipses;
		private List<Envelope.Builder> _envelopes;
		private List<buri.ddmsence.ddms.summary.tspi.Point.Builder> _points;
		private List<buri.ddmsence.ddms.summary.tspi.Polygon.Builder> _polygons;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(BoundingGeometry geometry) {
			for (Polygon polygon : geometry.getGmlPolygons())
				getGmlPolygons().add(new Polygon.Builder(polygon));
			for (Point point : geometry.getGmlPoints())
				getGmlPoints().add(new Point.Builder(point));
			for (ITspiShape shape : geometry.getTspiShapes()) {
				if (shape instanceof Circle) {
					getCircles().add(new Circle.Builder((Circle) shape));
				}
				if (shape instanceof Ellipse) {
					getEllipses().add(new Ellipse.Builder((Ellipse) shape));
				}
				if (shape instanceof Envelope) {
					getEnvelopes().add(new Envelope.Builder((Envelope) shape));
				}
				if (shape instanceof buri.ddmsence.ddms.summary.tspi.Point) {
					getPoints().add(
						new buri.ddmsence.ddms.summary.tspi.Point.Builder((buri.ddmsence.ddms.summary.tspi.Point) shape));
				}
				if (shape instanceof buri.ddmsence.ddms.summary.tspi.Polygon) {
					getPolygons().add(
						new buri.ddmsence.ddms.summary.tspi.Polygon.Builder(
							(buri.ddmsence.ddms.summary.tspi.Polygon) shape));
				}
			}
		}

		/**
		 * @see IBuilder#commit()
		 */
		public BoundingGeometry commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			if (!DDMSVersion.getCurrentVersion().isAtLeast("5.0")) {
				List<Polygon> polygons = new ArrayList<Polygon>();
				for (Polygon.Builder builder : getGmlPolygons()) {
					Polygon polygon = builder.commit();
					if (polygon != null)
						polygons.add(polygon);
				}
				List<Point> points = new ArrayList<Point>();
				for (Point.Builder builder : getGmlPoints()) {
					Point point = builder.commit();
					if (point != null)
						points.add(point);
				}
				return (new BoundingGeometry(polygons, points));
			}
			else {
				List<ITspiShape> shapes = new ArrayList<ITspiShape>();
				for (Circle.Builder builder : getCircles()) {
					Circle circle = builder.commit();
					if (circle != null)
						shapes.add(circle);
				}
				for (Ellipse.Builder builder : getEllipses()) {
					Ellipse ellipse = builder.commit();
					if (ellipse != null)
						shapes.add(ellipse);
				}
				for (Envelope.Builder builder : getEnvelopes()) {
					Envelope envelope = builder.commit();
					if (envelope != null)
						shapes.add(envelope);
				}
				for (buri.ddmsence.ddms.summary.tspi.Point.Builder builder : getPoints()) {
					buri.ddmsence.ddms.summary.tspi.Point point = builder.commit();
					if (point != null)
						shapes.add(point);
				}
				for (buri.ddmsence.ddms.summary.tspi.Polygon.Builder builder : getPolygons()) {
					buri.ddmsence.ddms.summary.tspi.Polygon polygon = builder.commit();
					if (polygon != null)
						shapes.add(polygon);
				}
				return (new BoundingGeometry(shapes));
			}
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getChildBuilders()) {
				hasValueInList = hasValueInList || !builder.isEmpty();
			}
			return (!hasValueInList);
		}

		/**
		 * Convenience method to get every child Builder in this Builder.
		 * 
		 * @return a list of IBuilders
		 */
		private List<IBuilder> getChildBuilders() {
			List<IBuilder> list = new ArrayList<IBuilder>();
			list.addAll(getGmlPolygons());
			list.addAll(getGmlPoints());
			list.addAll(getCircles());
			list.addAll(getEllipses());
			list.addAll(getEnvelopes());
			list.addAll(getPoints());
			list.addAll(getPolygons());
			return (list);
		}

		/**
		 * Builder accessor for the GML polygons in this geometry.
		 */
		public List<Polygon.Builder> getGmlPolygons() {
			if (_gmlPolygons == null)
				_gmlPolygons = new LazyList(Polygon.Builder.class);
			return _gmlPolygons;
		}

		/**
		 * Builder accessor for the GML points in this geometry.
		 */
		public List<Point.Builder> getGmlPoints() {
			if (_gmlPoints == null)
				_gmlPoints = new LazyList(Point.Builder.class);
			return _gmlPoints;
		}
		
		/**
		 * Builder accessor for the circles in this geometry.
		 */
		public List<Circle.Builder> getCircles() {
			if (_circles == null)
				_circles = new LazyList(Circle.Builder.class);
			return _circles;
		}
		
		/**
		 * Builder accessor for the ellipses in this geometry.
		 */
		public List<Ellipse.Builder> getEllipses() {
			if (_ellipses == null)
				_ellipses = new LazyList(Ellipse.Builder.class);
			return _ellipses;
		}
		
		/**
		 * Builder accessor for the envelopes in this geometry.
		 */
		public List<Envelope.Builder> getEnvelopes() {
			if (_envelopes == null)
				_envelopes = new LazyList(Envelope.Builder.class);
			return _envelopes;
		}
		
		/**
		 * Builder accessor for the points in this geometry.
		 */
		public List<buri.ddmsence.ddms.summary.tspi.Point.Builder> getPoints() {
			if (_points == null)
				_points = new LazyList(buri.ddmsence.ddms.summary.tspi.Point.Builder.class);
			return _points;
		}

		/**
		 * Builder accessor for the polygons in this geometry.
		 */
		public List<buri.ddmsence.ddms.summary.tspi.Polygon.Builder> getPolygons() {
			if (_polygons == null)
				_polygons = new LazyList(buri.ddmsence.ddms.summary.tspi.Polygon.Builder.class);
			return _polygons;
		}
	}
}