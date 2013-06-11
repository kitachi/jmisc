-- Pre-req: all play server using this db as 
--          a data source is shut down.
--
-- ensure there's only a single row in the
-- play evolution table.
create table tmp_play_evolutions as select * from play_evolutions limit 1;

truncate table play_evolutions;

drop table play_evolutions;

rename table tmp_play_evolutions to play_evolutions;

-- empty the apply_script and revert_script
-- reset state and last_problem.
update play_evolutions
set apply_script = '',
    revert_script = '',
    state = 'applied',
    last_problem = '';
