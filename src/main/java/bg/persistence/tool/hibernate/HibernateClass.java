package bg.persistence.tool.hibernate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlValue;

import bg.persistence.tool.springiser.ClassGeneratorItem;



/**
 * Doc :
 * https://docs.jboss.org/hibernate/stable/annotations/reference/en/html/xml-overriding.html
 * 
 * @author bertrand.guiral.ext
 *
 */
public class HibernateClass {

	@XmlAttribute(name = "name")
	private String name;

	@XmlAttribute(name = "class")
	private String entityClass;

//	@XmlElement
	protected Table table;

//	@XmlElement
	@SuppressWarnings("unused")
	private Inheritance inheritance = new Inheritance();

	@XmlElement
	private Attributes attributes = new Attributes();

	public HibernateClass(ClassGeneratorItem cei ) {
		super();
		this.name = cei.getClazz().getSimpleName().toLowerCase();
		this.table = new Table();
		table.name = cei.getClazz().getSimpleName().toUpperCase();
		this.entityClass = cei.getPackagePojo()+"."+cei.getClazz().getSimpleName();
		for (Field field : cei.getClazz().getDeclaredFields()) {

			Field_H field_H = new Field_H(field);
			if (field.getName().equalsIgnoreCase("id")) {

			} else if (isEnumeration(field)) {
				field_H.enumerated = new Enumerated();
				attributes.listFields.add(field_H);
			} else if (isOneToMany(field)) {
				field_H.type = null;
				
				field_H.mappedBy = getOneToManyMappedBy(cei.getClazz(), field);
				if (field_H.mappedBy != null) {
					field_H.cascade = CASCADE_REMOVE;
					//field_H.cascade = null;// debug
				}
				attributes.listOneToMany.add(field_H);
			} else if (isSimpleType(field)) {
				attributes.listFields.add(field_H);

			} else {
				field_H.type = null;
				// f.clazz=field.getType().getName();
				// f.property_ref=field.getType().getSimpleName().toLowerCase();
				// f.foreign_key=f.name+"Id";
				field_H.fetch="LAZY";
				
				attributes.listOneToOneAndManyToOne.add(field_H);
			}
		}
	}

	private String getOneToManyMappedBy(Class<?> clazz, Field field) {
		
		Type tRemote = getBoxedType(field);
		
		Class<?> clazzRemote = (Class<?>) tRemote;
		List<String> listMApped = new ArrayList<>();
		if (clazzRemote != null) {
			for (Field fRemote : clazzRemote.getDeclaredFields()) {
				Class<?> classTypeRemote = fRemote.getType();
				if (classTypeRemote.getName().equals(clazz.getName())) {
					listMApped.add(fRemote.getName());
				}
			}

		}
		if (listMApped.size() > 0) {
			// TODO si il ya en a 2 eligibles, trouver des critères sur les noms
			return listMApped.get(0);
		}
		return null;
	}

	public static Type getBoxedType(Field field) {

		Type type = field.getGenericType();
		if (type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
			Type tReturn = null;
			for (Type t : pt.getActualTypeArguments()) {
				tReturn = t;
			}
			return tReturn;
		}
		return null;
	}

	private boolean isEnumeration(Field field) {
		if (field.getType().isEnum()) {
			return true;
		}
		return false;
	}

	private static boolean isOneToMany(Field field) {
		if (field.getType() == List.class) {
			return true;
		}
		if (field.getType() == Set.class) {
			return true;
		}
		return false;
	}

	private static boolean isSimpleType(Field f) {
		return getSimpleTypeFromField(f) != null;

	}

	private static class ID_H {
		
		@XmlAttribute(name = "name")
		String name = "id";
		@XmlElement(name = "generated-value")
		Generator_H generator = new Generator_H();
	}

	
	
	private static class Generator_H {

		@XmlAttribute(name = "strategy")
		String name = null;//"IDENTITY" ; //"SEQUENCE";// "AUTO"
	}

	private static String getSimpleTypeFromField(Field f) {
		if (f.getName().equalsIgnoreCase("id")) {
			return null;
		}
		if (f.getType() == String.class) {
			return "string";
		}
		if (f.getType() == Integer.class) {
			return "Int";
		}
		if (f.getType() == Long.class) {
			return "Long";
		}
		if (f.getType() == Boolean.class) {
			return "Boolean";
		}
		if (f.getType().toString().equalsIgnoreCase("int")) {
			return "Int";
		}
		if (f.getType().toString().equalsIgnoreCase("boolean")) {
			return "boolean";
		}
		if (f.getType().toString().equalsIgnoreCase("long")) {
			return "Long";
		}
		if (f.getType().toString().equalsIgnoreCase("double")) {
			return "Double";
		}

		return getKnownType(f);
	}

	private static String getKnownType(Field f) {
		if (f.getType() == Duration.class) {
			return "BIGINT";
		}
		if (f.getType() == Instant.class) {
			return "TIMESTAMP";
		}

		return null;
	}

	
	@SuppressWarnings("unused")
	private static String getType(Field f) {
		if (isSimpleType(f)) {
			return getSimpleTypeFromField(f);
		}
		if (isOneToMany(f)) {
			return "" + getFieldGeneric(f).getSimpleName();
		}
		return "" + f.getType().getSimpleName();
	}

	public static Class<?> getFieldGeneric(Field field) {
		ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
		Class<?> clazzList = (Class<?>) stringListType.getActualTypeArguments()[0];
		return clazzList;

	}

	private static class Field_H {
		
		public Field_H(Field f) {
			//name = f.getName().toLowerCase();
			String n = f.getName();
			name = n != null && n.equals(n.toUpperCase()) ? n.toLowerCase() : n;
			if (isTimeStamp(f)) {
				temporal = "TIMESTAMP";
			}
		}

		private boolean isTimeStamp(Field f) {
			if (f.getType() == Date.class) {
				return true;
			}
			return false;
		}

		
		@XmlAttribute(name = "name")
		String name = "id";
		@XmlAttribute(name = "type")
		String type = null;
		@XmlAttribute(name = "class")
		String clazz = null;
		@XmlAttribute(name = "property-ref")
		String property_ref = null;
		@XmlAttribute(name = "foreign-key")
		String foreign_key = null;

		@XmlAttribute(name = "mapped-by")
		String mappedBy = null;
		
		@XmlAttribute(name = "fetch")
		String fetch=null;

		@XmlElement(name = "temporal")
		String temporal;

		@XmlElement(name = "enumerated")
		Enumerated enumerated;
		
		@XmlElement(name = "cascade")
		public Cascade cascade;


	}

	private static class Enumerated {
		@XmlValue
		String type = "STRING";
	}

	private static class Attributes {
		@XmlElement
		ID_H id = new ID_H();

		@XmlElement(name = "basic")
		List<Field_H> listFields = new ArrayList<>();

		@XmlElement(name = "many-to-one")
		List<Field_H> listOneToOneAndManyToOne = new ArrayList<>();

		@XmlElement(name = "one-to-many")
		List<Field_H> listOneToMany = new ArrayList<>();

	}

	private static class Table {
		@XmlAttribute(name = "name")
		String name = null;
	}

	// <inheritance strategy="TABLE_PER_CLASS"/>
	private static class Inheritance {
		@XmlAttribute(name = "strategy")
		String strategy = "TABLE_PER_CLASS";
	}
	
	private static Cascade CASCADE_REMOVE = new Cascade();
	
	private static class Cascade {
		@XmlElement(name = "cascade-remove")
		CascadeType cascadeRemove =new CascadeType();
	}
	
	private static class CascadeType {
		
		
	}

}
