package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@SuppressWarnings("serial")
@Entity
@Table(name = "dlThing")
public class Thing extends Model {
    public static Finder<Long, Thing> find = new Finder<Long, Thing>(Long.class, Thing.class);
    @Id
    public Long id;
    
    @Column(name = "type")
    public String tType;
    
    @Column(name = "subType")
    public String subType;
    
}
