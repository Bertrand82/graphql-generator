package bg.persistence.tool.springiser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;




public class ManagerClassEntities {

	public static ManagerClassEntities instance = new ManagerClassEntities();

	private List<ClassGeneratorItem> listBaseClassEntityItems = new ArrayList<>();
	private List<ClassGeneratorItem> listAllClassEntityItems = new ArrayList<>();

	private ManagerClassEntities() {
	}

	public List<ClassGeneratorItem> getListBaseClassEntityItems() {
		return listBaseClassEntityItems;
	}
 
	public void add(ClassGeneratorItem classEntityItem) {
		listBaseClassEntityItems.add(classEntityItem);
	}
	
	public static  TreeSet<ClassGeneratorItem> getListSortedClassEntityItemsByPackage(List<ClassGeneratorItem> list) {
		Comparator<ClassGeneratorItem> comparator = new Comparator<ClassGeneratorItem>() {

			@Override
			public int compare(ClassGeneratorItem o1, ClassGeneratorItem o2) {

				return o1.getClazz().getName().compareTo(o2.getClazz().getName());
			}
		};
		TreeSet<ClassGeneratorItem> set = new TreeSet<>(comparator);
		set.addAll(list);
		return set;
	}

	public void setListAllClasses(List<ClassGeneratorItem> listAll) {
		this.listAllClassEntityItems=listAll;
	}
	
	
	public void processBasePojo(String packageNameBase )	{
		for (ClassGeneratorItem c : listAllClassEntityItems) {
			if (c.getClazz().getPackage().getName().equals(packageNameBase)) {
				listBaseClassEntityItems.add(c);
			}
		}
	}

	public List<ClassGeneratorItem> getListClassEntityItemsByPackage(String packageVersion) {
		List<ClassGeneratorItem> list = new ArrayList<>();
		for (ClassGeneratorItem c : listAllClassEntityItems) {
			if (c.getClazz().getPackage().getName().equals(packageVersion)) {
				list.add(c);
			}
		}
		return list;
	}
	

	

}

/* EOF */
