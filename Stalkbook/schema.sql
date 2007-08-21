DROP TABLE location_stalker;
DROP TABLE location;
DROP TABLE stalker;


CREATE TABLE location (
  coord_x float8,
  coord_y float8,
  loc_name VARCHAR(255),
  PRIMARY KEY (coord_x, coord_y)
);

CREATE INDEX location_coords_idx ON location
       USING GIST (box(point(coord_x, coord_y),point(coord_x, coord_y)) box_ops);

CREATE TABLE stalker (
  fb_username VARCHAR(255) PRIMARY KEY,
  home_coord_x float8,
  home_coord_y float8
);

CREATE INDEX stalker_home_coords_idx ON stalker
       USING GIST (box(point(home_coord_x, home_coord_y),point(home_coord_x, home_coord_y)) box_ops);

CREATE TABLE location_stalker (
  coord_x float8,
  coord_y float8,
  fb_username VARCHAR(255),
  PRIMARY KEY (coord_x, coord_y, fb_username),
  FOREIGN KEY (fb_username) REFERENCES stalker,
  FOREIGN KEY (coord_x, coord_y) REFERENCES location
);