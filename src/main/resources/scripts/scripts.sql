create database db_zip_code;

use db_zip_code;

create table zip_code (
                          zip varchar(10) primary key,    -- Código postal (clave primaria)
                          city varchar(100),              -- Ciudad
                          county varchar(100),            -- Condado
                          state varchar(2),               -- Estado (abreviatura, por ejemplo: CA, NY)
                          timezone varchar(10),           -- Zona horaria (puede ser nombre o número, depende del uso)
                          type varchar(20)                -- Tipo (por ejemplo: STANDARD, PO BOX, etc.)
);