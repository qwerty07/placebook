DROP TABLE location_stalker;
DROP TABLE photo;
DROP TABLE comment;
DROP TABLE location;
DROP TABLE stalker;


CREATE TABLE location (
  coord_x float8,
  coord_y float8,
  loc_name VARCHAR(255) NOT NULL,
  description TEXT,
  PRIMARY KEY (coord_x, coord_y)
);

CREATE INDEX location_coords_idx ON location
       USING GIST (box(point(coord_x, coord_y),point(coord_x, coord_y)) box_ops);

CREATE TABLE stalker (
  fb_id varchar(255) PRIMARY KEY,
  fb_name VARCHAR(255) NOT NULL,
  fb_pic VARCHAR(255),
  home_coord_x float8,
  home_coord_y float8
);

CREATE INDEX stalker_home_coords_idx ON stalker
       USING GIST (box(point(home_coord_x, home_coord_y),point(home_coord_x, home_coord_y)) box_ops);

CREATE TABLE location_stalker (
  coord_x float8,
  coord_y float8,
  stalker_fb_id VARCHAR(255),
  PRIMARY KEY (coord_x, coord_y, stalker_fb_id),
  FOREIGN KEY (stalker_fb_id) REFERENCES stalker(fb_id) ON DELETE CASCADE,
  FOREIGN KEY (coord_x, coord_y) REFERENCES location ON DELETE CASCADE
);

CREATE TABLE photo (
  photo_id serial,
  coord_x float8 NOT NULL,
  coord_y float8 NOT NULL,
  stalker_fb_id varchar(255) NOT NULL,
  description text,
  image OID NOT NULL,
  contributed timestamp with time zone DEFAULT now() NOT NULL,
  FOREIGN KEY (stalker_fb_id) REFERENCES stalker(fb_id) ON DELETE CASCADE,
  FOREIGN KEY (coord_x, coord_y) REFERENCES location ON DELETE CASCADE
);

CREATE TABLE comment (
  coord_x float8 NOT NULL,
  coord_y float8 NOT NULL,
  stalker_fb_id varchar(255) NOT NULL,
  comment text NOT NULL,
  contributed timestamp with time zone DEFAULT now() NOT NULL,
  FOREIGN KEY (stalker_fb_id)  REFERENCES stalker(fb_id) ON DELETE CASCADE,
  FOREIGN KEY (coord_x, coord_y) REFERENCES location ON DELETE CASCADE
);