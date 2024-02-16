insert into datapoint (group_id, indicator_code, long_value, numeric_value, text_value)
values
(1, 'code1', 1, null, 'text11')
on conflict(group_id, indicator_code) do update
set
    long_value = excluded.long_value,
    numeric_value = excluded.numeric_value,
    text_value = excluded.text_value,
    update_time = now();
