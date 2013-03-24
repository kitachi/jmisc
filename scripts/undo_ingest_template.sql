# undo ingest sql script template
# --- !Ups
start transaction;
delete from dlRelationship where id = <relationshipID>;
delete from dlFileLocation where id = <fileLocationID>;
delete from dlThingCopyFile where id = <thingCopyFileID>;
delete from dlThingCopy where id = <thingCopyID>;
delete from dlThing where id = <thingID>;

# -- log ingest job status
insert into dlIngestStatus(jobId, status, statusDescription)
values(<jobId>, 2, 'Cleaned out the ingest.');
commit;

# --- !Downs 

