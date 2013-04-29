package models.ingest;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.avaje.ebean.Query;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

@SuppressWarnings("serial")
@Entity
@Table(name = "dlIngestStatus")
public class JobStatus extends Model {
    public static final int UUID_IDX = 0;
    public static final int TS_IDX = 1;

    @Id
    public Long id;

    @Column(name = "jobTS")
    public String jobTS;

    @Column(name = "jobName")
    public String jobName;

    @Column(name = "topUUID")
    public String topUUID;

    @Column(name = "status")
    public int status;

    @Column(name = "statusDescription")
    public String statusDescription;

    @Column(name = "startDate")
    public Date startDate;

    @Column(name = "endDate")
    public Date endDate;

    public static Finder<Long, JobStatus> find = new Finder<Long, JobStatus>(
            Long.class, JobStatus.class);

    public static Finder<String[], JobStatus> findByUUIDTS = new Finder<String[], JobStatus>(
            String[].class, JobStatus.class) {
        // find JobStatus by UUID and TS
        @Override
        public JobStatus byId(String[] params) {
            Query<JobStatus> qry = JobStatus.find.where(
                    "topUUID = '" + params[UUID_IDX] + "' and jobTS = '"
                            + params[TS_IDX] + "'").orderBy("jobNo Desc");
            switch (qry.findRowCount()) {
            case 0:
                return null;
            default:
                return qry.findList().get(0);
            }
        }
    };
}
