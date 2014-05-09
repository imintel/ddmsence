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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nu.xom.Element;
import nu.xom.Elements;
import buri.ddmsence.AbstractBaseComponent;
import buri.ddmsence.ddms.IBuilder;
import buri.ddmsence.ddms.IDDMSComponent;
import buri.ddmsence.ddms.InvalidDDMSException;
import buri.ddmsence.ddms.ValidationMessage;
import buri.ddmsence.ddms.security.ism.SecurityAttributes;
import buri.ddmsence.util.DDMSVersion;
import buri.ddmsence.util.LazyList;
import buri.ddmsence.util.Util;

/**
 * An immutable implementation of ddms:subjectCoverage.
 * <br /><br />
 * {@ddms.versions 11111}
 * 
 * <p></p>
 * 
 * {@table.header History}
 * 		<p>Before DDMS 4.0.1, a subjectCoverage element contains a locally defined Subject construct. This construct is a
 * 		container for the keywords and categories of a resource. It exists only inside of a ddms:subjectCoverage parent,
 * 		so it is not implemented as a Java object. Starting in DDMS 4.0.1, the Subject wrapper has been removed.</p>
 * {@table.footer}
 * {@table.header Nested Elements}
 * 		{@child.info ddms:category|0..*|11111}
 * 		{@child.info ddms:keyword|0..*|11111}	
 * 		{@child.info ddms:productionMetric|0..*|00011}	
 * 		{@child.info ddms:nonStateActor|0..*|00011}		
 * {@table.footer}
 * {@table.header Attributes}
 * 		{@child.info ism:&lt;<i>securityAttributes</i>&gt;|0..*|01111}
 * {@table.footer}
 * {@table.header Validation Rules}
 * 		{@ddms.rule The qualified name of this element must be correct.|Error|11111}
 * 		{@ddms.rule At least one of ddms:keyword or ddms:category must exist.|Error|11111}
 * 		{@ddms.rule Security attributes must not be used before the DDMS version in which they were introduced.|Error|11111}
 * 		{@ddms.rule 1 or more keywords have the same value.|Warning|11111}
 * 		{@ddms.rule 1 or more categories have the same value.|Warning|11111}
 * 		{@ddms.rule 1 or more production metrics have the same value.|Warning|11111}
 * {@table.footer}
 * 
 * @author Brian Uri!
 * @since 0.9.b
 */
public final class SubjectCoverage extends AbstractBaseComponent {

	private List<Keyword> _keywords = null;
	private List<Category> _categories = null;
	private List<ProductionMetric> _productionMetrics = null;
	private List<NonStateActor> _nonStateActors = null;
	private SecurityAttributes _securityAttributes = null;

	private static final String SUBJECT_NAME = "Subject";

	/**
	 * Constructor for creating a component from a XOM Element
	 * 
	 * @param element the XOM element representing this
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public SubjectCoverage(Element element) throws InvalidDDMSException {
		try {
			Util.requireDDMSValue("subjectCoverage element", element);
			setXOMElement(element, false);
			Element subjectElement = getSubjectElement();
			_keywords = new ArrayList<Keyword>();
			_categories = new ArrayList<Category>();
			_productionMetrics = new ArrayList<ProductionMetric>();
			_nonStateActors = new ArrayList<NonStateActor>();
			if (subjectElement != null) {
				Elements keywords = subjectElement.getChildElements(Keyword.getName(getDDMSVersion()), getNamespace());
				for (int i = 0; i < keywords.size(); i++) {
					_keywords.add(new Keyword(keywords.get(i)));
				}
				Elements categories = subjectElement.getChildElements(Category.getName(getDDMSVersion()),
					getNamespace());
				for (int i = 0; i < categories.size(); i++) {
					_categories.add(new Category(categories.get(i)));
				}
				Elements metrics = subjectElement.getChildElements(ProductionMetric.getName(getDDMSVersion()),
					getNamespace());
				for (int i = 0; i < metrics.size(); i++) {
					_productionMetrics.add(new ProductionMetric(metrics.get(i)));
				}
				Elements actors = subjectElement.getChildElements(NonStateActor.getName(getDDMSVersion()),
					getNamespace());
				for (int i = 0; i < actors.size(); i++) {
					_nonStateActors.add(new NonStateActor(actors.get(i)));
				}
			}
			_securityAttributes = new SecurityAttributes(element);
			validate();
		}
		catch (InvalidDDMSException e) {
			e.setLocator(getQualifiedName());
			throw (e);
		}
	}

	/**
	 * Constructor for creating a component from raw data
	 * 
	 * @param keywords list of keywords
	 * @param categories list of categories
	 * @param productionMetrics list of metrics
	 * @param nonStateActors list of actors
	 * @param securityAttributes any security attributes
	 * @throws InvalidDDMSException if any required information is missing or malformed
	 */
	public SubjectCoverage(List<Keyword> keywords, List<Category> categories, List<ProductionMetric> productionMetrics,
		List<NonStateActor> nonStateActors, SecurityAttributes securityAttributes) throws InvalidDDMSException {
		try {
			if (keywords == null)
				keywords = Collections.emptyList();
			if (categories == null)
				categories = Collections.emptyList();
			if (productionMetrics == null)
				productionMetrics = Collections.emptyList();
			if (nonStateActors == null)
				nonStateActors = Collections.emptyList();
			Element element = Util.buildDDMSElement(SubjectCoverage.getName(DDMSVersion.getCurrentVersion()), null);

			Element subjectElement = DDMSVersion.getCurrentVersion().isAtLeast("4.0.1") ? element
				: Util.buildDDMSElement(SUBJECT_NAME, null);
			for (Keyword keyword : keywords)
				subjectElement.appendChild(keyword.getXOMElementCopy());
			for (Category category : categories)
				subjectElement.appendChild(category.getXOMElementCopy());
			for (ProductionMetric metric : productionMetrics)
				subjectElement.appendChild(metric.getXOMElementCopy());
			for (NonStateActor actor : nonStateActors)
				subjectElement.appendChild(actor.getXOMElementCopy());

			if (!DDMSVersion.getCurrentVersion().isAtLeast("4.0.1"))
				element.appendChild(subjectElement);

			_keywords = keywords;
			_categories = categories;
			_productionMetrics = productionMetrics;
			_nonStateActors = nonStateActors;
			_securityAttributes = SecurityAttributes.getNonNullInstance(securityAttributes);
			_securityAttributes.addTo(element);
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
		Util.requireDDMSQName(getXOMElement(), SubjectCoverage.getName(getDDMSVersion()));
		Element subjectElement = getSubjectElement();
		Util.requireDDMSValue("Subject element", subjectElement);
		String namespace = subjectElement.getNamespaceURI();
		int count = subjectElement.getChildElements(Keyword.getName(getDDMSVersion()), namespace).size()
			+ subjectElement.getChildElements(Category.getName(getDDMSVersion()), namespace).size();
		if (count < 1)
			throw new InvalidDDMSException("At least 1 keyword or category must exist.");
		if (!getDDMSVersion().isAtLeast("3.0") && !getSecurityAttributes().isEmpty()) {
			throw new InvalidDDMSException(
				"Security attributes must not be applied to this component until DDMS 3.0 or later.");
		}
		super.validate();
	}

	/**
	 * @see AbstractBaseComponent#validateWarnings()
	 */
	protected void validateWarnings() {
		Set<Keyword> uniqueKeywords = new HashSet<Keyword>(getKeywords());
		if (uniqueKeywords.size() != getKeywords().size())
			addWarning("1 or more keywords have the same value.");
		Set<Category> uniqueCategories = new HashSet<Category>(getCategories());
		if (uniqueCategories.size() != getCategories().size())
			addWarning("1 or more categories have the same value.");
		Set<ProductionMetric> uniqueMetrics = new HashSet<ProductionMetric>(getProductionMetrics());
		if (uniqueMetrics.size() != getProductionMetrics().size())
			addWarning("1 or more productionMetrics have the same value.");
		super.validateWarnings();
	}

	/**
	 * @see AbstractBaseComponent#getLocatorSuffix()
	 */
	protected String getLocatorSuffix() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? "" : ValidationMessage.ELEMENT_PREFIX
			+ getXOMElement().getNamespacePrefix() + ":" + SUBJECT_NAME);
	}

	/**
	 * @see AbstractBaseComponent#getOutput(boolean, String, String)
	 */
	public String getOutput(boolean isHTML, String prefix, String suffix) {
		String localPrefix = buildPrefix(prefix, getName(), suffix + ".");
		if (!getDDMSVersion().isAtLeast("4.0.1"))
			localPrefix += SUBJECT_NAME + ".";
		StringBuffer text = new StringBuffer();
		text.append(buildOutput(isHTML, localPrefix, getKeywords()));
		text.append(buildOutput(isHTML, localPrefix, getCategories()));
		text.append(buildOutput(isHTML, localPrefix, getProductionMetrics()));
		text.append(buildOutput(isHTML, localPrefix, getNonStateActors()));
		text.append(getSecurityAttributes().getOutput(isHTML, localPrefix));
		return (text.toString());
	}

	/**
	 * @see AbstractBaseComponent#getNestedComponents()
	 */
	protected List<IDDMSComponent> getNestedComponents() {
		List<IDDMSComponent> list = new ArrayList<IDDMSComponent>();
		list.addAll(getKeywords());
		list.addAll(getCategories());
		list.addAll(getProductionMetrics());
		list.addAll(getNonStateActors());
		return (list);
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!super.equals(obj) || !(obj instanceof SubjectCoverage))
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
		return ("subjectCoverage");
	}

	/**
	 * Accessor for the element which contains the keywords and categories. Before DDMS 4.0.1, this is a wrapper element
	 * called ddms:Subject. Starting in DDMS 4.0.1, it is the ddms:subjectCoverage element itself.
	 */
	private Element getSubjectElement() {
		return (getDDMSVersion().isAtLeast("4.0.1") ? getXOMElement() : getChild(SUBJECT_NAME));
	}

	/**
	 * Accessor for the keywords (0 to many).
	 * 
	 * @return unmodifiable List
	 */
	public List<Keyword> getKeywords() {
		return (Collections.unmodifiableList(_keywords));
	}

	/**
	 * Accessor for the categories (0 to many).
	 * 
	 * @return unmodifiable List
	 */
	public List<Category> getCategories() {
		return (Collections.unmodifiableList(_categories));
	}

	/**
	 * Accessor for the production metrics (0 to many).
	 * 
	 * @return unmodifiable List
	 */
	public List<ProductionMetric> getProductionMetrics() {
		return (Collections.unmodifiableList(_productionMetrics));
	}

	/**
	 * Accessor for the non-state actors (0 to many).
	 * 
	 * @return unmodifiable List
	 */
	public List<NonStateActor> getNonStateActors() {
		return (Collections.unmodifiableList(_nonStateActors));
	}

	/**
	 * Accessor for the Security Attributes. Will always be non-null, even if it has no values set.
	 */
	public SecurityAttributes getSecurityAttributes() {
		return (_securityAttributes);
	}

	/**
	 * Builder for this DDMS component.
	 * 
	 * @see IBuilder
	 * @author Brian Uri!
	 * @since 1.8.0
	 */
	public static class Builder implements IBuilder, Serializable {
		private static final long serialVersionUID = -1550204187042536412L;
		private List<Keyword.Builder> _keywords;
		private List<Category.Builder> _categories;
		private List<ProductionMetric.Builder> _productionMetrics;
		private List<NonStateActor.Builder> _nonStateActors;
		private SecurityAttributes.Builder _securityAttributes;

		/**
		 * Empty constructor
		 */
		public Builder() {}

		/**
		 * Constructor which starts from an existing component.
		 */
		public Builder(SubjectCoverage coverage) {
			for (Keyword keyword : coverage.getKeywords())
				getKeywords().add(new Keyword.Builder(keyword));
			for (Category category : coverage.getCategories())
				getCategories().add(new Category.Builder(category));
			for (ProductionMetric metric : coverage.getProductionMetrics())
				getProductionMetrics().add(new ProductionMetric.Builder(metric));
			for (NonStateActor actor : coverage.getNonStateActors())
				getNonStateActors().add(new NonStateActor.Builder(actor));
			setSecurityAttributes(new SecurityAttributes.Builder(coverage.getSecurityAttributes()));
		}

		/**
		 * @see IBuilder#commit()
		 */
		public SubjectCoverage commit() throws InvalidDDMSException {
			if (isEmpty())
				return (null);
			List<Category> categories = new ArrayList<Category>();
			for (Category.Builder builder : getCategories()) {
				Category category = builder.commit();
				if (category != null)
					categories.add(category);
			}
			List<Keyword> keywords = new ArrayList<Keyword>();
			for (Keyword.Builder builder : getKeywords()) {
				Keyword keyword = builder.commit();
				if (keyword != null)
					keywords.add(keyword);
			}
			List<ProductionMetric> metrics = new ArrayList<ProductionMetric>();
			for (ProductionMetric.Builder builder : getProductionMetrics()) {
				ProductionMetric metric = builder.commit();
				if (metric != null)
					metrics.add(metric);
			}
			List<NonStateActor> actors = new ArrayList<NonStateActor>();
			for (NonStateActor.Builder builder : getNonStateActors()) {
				NonStateActor actor = builder.commit();
				if (actor != null)
					actors.add(actor);
			}
			return (new SubjectCoverage(keywords, categories, metrics, actors, getSecurityAttributes().commit()));
		}

		/**
		 * @see IBuilder#isEmpty()
		 */
		public boolean isEmpty() {
			boolean hasValueInList = false;
			for (IBuilder builder : getChildBuilders()) {
				hasValueInList = hasValueInList || !builder.isEmpty();
			}
			return (!hasValueInList && getSecurityAttributes().isEmpty());
		}

		/**
		 * Convenience method to get every child Builder in this Builder.
		 * 
		 * @return a list of IBuilders
		 */
		private List<IBuilder> getChildBuilders() {
			List<IBuilder> list = new ArrayList<IBuilder>();
			list.addAll(getKeywords());
			list.addAll(getCategories());
			list.addAll(getProductionMetrics());
			list.addAll(getNonStateActors());
			return (list);
		}

		/**
		 * Builder accessor for the keywords in this coverage.
		 */
		public List<Keyword.Builder> getKeywords() {
			if (_keywords == null)
				_keywords = new LazyList(Keyword.Builder.class);
			return _keywords;
		}

		/**
		 * Builder accessor for the categories in this coverage.
		 */
		public List<Category.Builder> getCategories() {
			if (_categories == null)
				_categories = new LazyList(Category.Builder.class);
			return _categories;
		}

		/**
		 * Builder accessor for the production metrics in this coverage.
		 */
		public List<ProductionMetric.Builder> getProductionMetrics() {
			if (_productionMetrics == null)
				_productionMetrics = new LazyList(ProductionMetric.Builder.class);
			return _productionMetrics;
		}

		/**
		 * Builder accessor for the non-state actors in this coverage.
		 */
		public List<NonStateActor.Builder> getNonStateActors() {
			if (_nonStateActors == null)
				_nonStateActors = new LazyList(NonStateActor.Builder.class);
			return _nonStateActors;
		}

		/**
		 * Builder accessor for the Security Attributes
		 */
		public SecurityAttributes.Builder getSecurityAttributes() {
			if (_securityAttributes == null)
				_securityAttributes = new SecurityAttributes.Builder();
			return _securityAttributes;
		}

		/**
		 * Builder accessor for the Security Attributes
		 */
		public void setSecurityAttributes(SecurityAttributes.Builder securityAttributes) {
			_securityAttributes = securityAttributes;
		}
	}
}