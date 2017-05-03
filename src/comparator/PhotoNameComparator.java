package comparator;

import java.util.Comparator;

import model.Photo;

public class PhotoNameComparator implements Comparator<Photo> {

	@Override
	public int compare(Photo o1, Photo o2) {
		// TODO Auto-generated method stub
		return o1.getName().compareToIgnoreCase(o2.getName());
	}

}
