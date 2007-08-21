DROP TABLE location_stalker;
DROP TABLE location;
DROP TABLE stalker;

CREATE TABLE location (
  coord_x DECIMAL,
  coord_y DECIMAL,
  loc_name VARCHAR(255),
  PRIMARY KEY (coord_x, coord_y)
);

CREATE INDEX location_coords_idx ON location
       USING GIST (box(point(coord_x, coord_y),point(coord_x, coord_y)) box_ops);

CREATE TABLE stalker (
  fb_username VARCHAR(255) PRIMARY KEY,
  home_coord_x DECIMAL,
  home_coord_y DECIMAL
);

CREATE INDEX location_coords_idx ON location
       USING GIST (box(point(home_coord_x, home_coord_y),point(home_coord_x, home_coord_y)) box_ops);

CREATE TABLE location_stalker (
  coord_x DECIMAL,
  coord_y DECIMAL,
  fb_username VARCHAR(255),
  PRIMARY KEY (coord_x, coord_y, fb_username),
  FOREIGN KEY (fb_username) REFERENCES stalker,
  FOREIGN KEY (coord_x, coord_y) REFERENCES location
);