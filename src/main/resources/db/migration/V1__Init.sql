-- Create the 'users' table
CREATE TABLE IF NOT EXISTS public.users (
    id BIGINT NOT NULL,
    email CHARACTER VARYING(100) COLLATE pg_catalog."default",
    password CHARACTER VARYING(100)  COLLATE pg_catalog."default",
    role CHARACTER VARYING(20) COLLATE pg_catalog."default",
    username CHARACTER VARYING(100)  COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id)
    );

-- Create the 'users' table seq
CREATE SEQUENCE IF NOT EXISTS public.users_seq
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Create the 'article' table
CREATE TABLE IF NOT EXISTS public.article (
    id BIGINT NOT NULL,
    header CHARACTER VARYING(255) COLLATE pg_catalog."default",
    publish_date DATE,
    short_desc CHARACTER VARYING(255) COLLATE pg_catalog."default",
    text CHARACTER VARYING(255) COLLATE pg_catalog."default",
    CONSTRAINT article_pkey PRIMARY KEY (id)
    );

-- Create the 'article' table seq
CREATE SEQUENCE IF NOT EXISTS public.article_seq
    INCREMENT 50
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

-- Create the 'article_users' table for many-to-many relationship
CREATE TABLE IF NOT EXISTS public.article_users (
                                                    article_id BIGINT NOT NULL,
                                                    user_id BIGINT NOT NULL,
                                                    CONSTRAINT article_users_pkey PRIMARY KEY (article_id, user_id),
    CONSTRAINT fk2flxf7stufgpaqosibicdopn3 FOREIGN KEY (user_id)
    REFERENCES public.users (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT fklw59vrcb9q659d34kh6fq54fk FOREIGN KEY (article_id)
    REFERENCES public.article (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );

-- Create the 'article_keywords' table for storing keywords
CREATE TABLE IF NOT EXISTS public.article_keywords (
                                                       article_id BIGINT NOT NULL,
                                                       keywords CHARACTER VARYING(50) COLLATE pg_catalog."default",
    CONSTRAINT fkimdomuah3ta4hmeb77e68m0wl FOREIGN KEY (article_id)
    REFERENCES public.article (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );
