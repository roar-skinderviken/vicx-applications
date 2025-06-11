ALTER TABLE calc_entry
ALTER COLUMN operation TYPE INT USING operation::integer;