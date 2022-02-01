create or replace procedure calculate_count_of_agents()
    language plpgsql
as
$$
BEGIN
    select count(col1) from agent;
END
$$
