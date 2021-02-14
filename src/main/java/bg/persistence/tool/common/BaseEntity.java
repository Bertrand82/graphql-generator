package bg.persistence.tool.common;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
 
@MappedSuperclass
public  class BaseEntity {
 
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		protected String id;
	
	   
		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
}