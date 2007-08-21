--CREATE FUNCTION complex_add(complex, complex)
--    RETURNS complex
--    AS 'filename', 'complex_add'
--    LANGUAGE C IMMUTABLE STRICT;

--CREATE OPERATOR + (
--    leftarg = complex,
--    rightarg = complex,
--    procedure = complex_add,
--    commutator = +
--);


-- CREATE OPERATION FUNCTION --
CREATE OR REPLACE FUNCTION point_lt(point, point) RETURNS boolean AS $$
       SELECT $1[0] < $2[0] OR ($1[0] = $2[0] AND $1[1] < $2[1]);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION point_le(point, point) RETURNS boolean AS $$
       SELECT $1[0] < $2[0] OR ($1[0] = $2[0] AND $1[1] <= $2[1]);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION point_gt(point, point) RETURNS boolean AS $$
       SELECT $1[0] > $2[0] OR ($1[0] = $2[0] AND $1[1] > $2[1]);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION point_ge(point, point) RETURNS boolean AS $$
       SELECT $1[0] > $2[0] OR ($1[0] = $2[0] AND $1[1] >= $2[1]);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION point_eq(point, point) RETURNS boolean AS $$
       SELECT $1 ~= $2;
$$ LANGUAGE SQL;

DROP OPERATOR CLASS point_ops USING btree;

-- CREATE OPERATOR ASSOCIATIONS --
DROP OPERATOR < (point, point);
CREATE OPERATOR < (
       PROCEDURE = point_lt,
       LEFTARG = point,
       RIGHTARG = point
--       commutator = >=
--       negator = >=
);

DROP OPERATOR <= (point, point);
CREATE OPERATOR <= (
       leftarg = point,
       rightarg = point,
       procedure = point_le
--       commutator = >
--       negator = >
);

DROP OPERATOR > (point, point);
CREATE OPERATOR > (
       leftarg = point,
       rightarg = point,
       procedure = point_gt
--       commutator = <=
--       negator = <=
);

DROP OPERATOR >= (point, point);
CREATE OPERATOR >= (
       leftarg = point,
       rightarg = point,
       procedure = point_ge
--       commutator = <
--       negator = <
);

DROP OPERATOR = (point, point);
CREATE OPERATOR = (
       leftarg = point,
       rightarg = point,
       procedure = point_eq
);


-- CREATE SUPPORT FUNCTION --
-- Compare two keys and return an integer less than zero, zero, or greater than zero, indicating whether the first key is less than, equal to, or greater than the second.
CREATE OR REPLACE FUNCTION point_btree_support(point, point) RETURNS integer AS $$
       SELECT -1 WHERE $1 < $2 UNION
       SELECT 0  WHERE $1 ~= $2 UNION
       SELECT 1  WHERE $1 > $2
$$ LANGUAGE SQL;


-- BIND OPERATOR / SUPPORT FUNCTIONS TO BTREE --
CREATE	 OPERATOR CLASS point_ops DEFAULT FOR TYPE point USING btree AS
       OPERATOR	      1		<,
       OPERATOR	      2		<=,
       OPERATOR	      3		~=,
       OPERATOR	      4		>=,
       OPERATOR	      5		>,
       FUNCTION	      1		point_btree_support(point, point);


-- Operation negation and commututor appear to not map correctly?

-- SELECT * FROM pg_operator;

-- DROP OPERATOR CLASS point_ops USING btree;
-- DROP OPERATOR < (point, point);
-- DROP OPERATOR <= (point, point);
-- DROP OPERATOR > (point, point);
-- DROP OPERATOR >= (point, point);