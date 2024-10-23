-- Table: public.bookings

-- DROP TABLE IF EXISTS public.bookings;

CREATE TABLE IF NOT EXISTS public.bookings
(
    date date NOT NULL,
    "time" time(6) without time zone,
    id bigint NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    CONSTRAINT bookings_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.bookings
    OWNER to postgres;