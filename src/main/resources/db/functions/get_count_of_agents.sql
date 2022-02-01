create or replace function get_count_of_agents()
    returns int
    language plpgsql
as
$$
declare
    agent_count integer;
BEGIN
    select count(id) into agent_count from agent;
    return agent_count;
END
$$
