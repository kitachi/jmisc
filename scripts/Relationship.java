package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
@Table(name = "dlRelationship")
public class Relationship extends Model {
    public static Finder<Long, Relationship> find = new Finder<Long, Relationship>(Long.class, Relationship.class);
    
    @Id
    public Long id;
    
    @Column(name = "thing1Id")
    public Long thing1Id;
    
    @Column(name = "relationship")
    public int relationship;
    
    @Column(name = "thing2Id")
    public Long thing2Id;
}
