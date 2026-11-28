-- =============================================================
-- ESQUEMA DE BASE DE DATOS — ms-clasificator (ADR-011)
-- Generado a partir de las entidades JPA con la generación de esquema de Hibernate
-- (jakarta.persistence.schema-generation.scripts) y revisado a mano.
--
-- La aplicación corre con spring.jpa.hibernate.ddl-auto=validate: Hibernate solo
-- verifica que este esquema coincida con las entidades y NUNCA modifica la BD.
-- Cualquier cambio de entidades debe reflejarse aquí (y en un ADR si es estructural).
--
-- Docker: lo ejecuta el contenedor de MySQL (docker-entrypoint-initdb.d) la primera
--         vez que el volumen está vacío, antes de 02-data.sql.
-- Local : mysql -u root -p --default-character-set=utf8mb4 db-clasificator < docker/init/01-schema.sql
--
-- Notas de diseño:
--   * dataset.name y dataset.medical_image_type_id NO son UNIQUE: la unicidad es solo
--     entre datasets activos (soft delete) y la valida el servicio.
--   * active + deleted_at (dataset, dataset_category, diagnostic_category_dataset):
--     active filtra y valida; deleted_at registra cuándo se retiró.
--   * diagnostic_category_dataset.created_at/deleted_at definen la vigencia de cada
--     diagnóstico en su categoría (definición histórica, ADR-011).
--   * medical_image_type: soft delete (ADR-013). active tiene DEFAULT 1 para que los
--     INSERT de 02-data.sql (que no lo nombran) creen tipos activos. name NO es UNIQUE:
--     la unicidad es solo entre tipos activos y la valida el servicio.
-- =============================================================

    create table clinical_record (
        id integer not null auto_increment,
        patient_id integer not null,
        visit_date datetime(6) not null,
        chief_complaint varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table dataset (
        active bit not null,
        id integer not null auto_increment,
        medical_diagnostic_id integer not null,
        medical_image_type_id integer not null,
        deleted_at datetime(6),
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table dataset_category (
        active bit not null,
        dataset_id integer not null,
        id integer not null auto_increment,
        num_value integer,
        deleted_at datetime(6),
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table diagnosis (
        clinical_record_id integer not null,
        id integer not null auto_increment,
        medical_diagnostic_id integer not null,
        primary key (id)
    ) engine=InnoDB;

    create table diagnostic_category_dataset (
        active bit not null,
        dataset_category_id integer not null,
        id integer not null,
        medical_diagnostic_id integer not null,
        created_at datetime(6) not null,
        deleted_at datetime(6),
        primary key (id)
    ) engine=InnoDB;

    create table diagnostic_category_dataset_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into diagnostic_category_dataset_seq ( next_val ) values ( 1 );

    create table doctor (
        id integer not null auto_increment,
        code varchar(255) not null,
        user_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table doctor_area (
        doctor_id integer not null,
        evaluation_area_id integer not null,
        id integer not null auto_increment,
        primary key (id)
    ) engine=InnoDB;

    create table evaluation_area (
        doctors_count integer,
        id integer not null auto_increment,
        code_area varchar(255) not null,
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table image_diagnostic (
        dataset_category_id integer,
        doctor_id integer not null,
        id integer not null auto_increment,
        medical_img_id integer not null,
        diagnostic_date datetime(6) not null,
        primary key (id)
    ) engine=InnoDB;

    create table image_doctor_diagnostics (
        id integer not null auto_increment,
        image_diagnostic_id integer not null,
        medical_diagnostic_id integer not null,
        primary key (id)
    ) engine=InnoDB;

    create table medical_img (
        clinical_record_id integer,
        id integer not null,
        medical_image_type_id integer not null,
        created_at datetime(6) not null,
        file_size bigint,
        provider varchar(20) not null,
        content_type varchar(50),
        image_key varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table medical_diagnostic (
        id integer not null auto_increment,
        parent_diagnostic_id integer,
        diagnostic_code varchar(255) not null,
        diagnostic_name varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table medical_image_type (
        active bit not null default 1,
        evaluation_area_id integer,
        id integer not null auto_increment,
        deleted_at datetime(6),
        name varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table patient (
        id integer not null auto_increment,
        dob datetime(6) not null,
        document varchar(255) not null,
        sex varchar(255) not null,
        user_id varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table patient_datum (
        clinical_record_id integer not null,
        id integer not null auto_increment,
        primitive_datum_id integer not null,
        description varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table primitive_datum (
        id integer not null,
        name varchar(255) not null,
        type enum ('BINARY','BOOLEAN','DATE','DATETIME','DOUBLE','INTEGER','STRING','TEXT','TIME') not null,
        unit enum ('BPM','CELSIUS','CENTIMETER','DAY','FAHRENHEIT','GRAM','HOUR','INTERNATIONAL_UNIT','KG_M2','KILOGRAM','LITER','METER','MG_DL','MICROGRAM','MILLIGRAM','MILLILITER','MILLIMETER','MINUTE','MMHG','MMOL_L','NONE','PERCENT','RESPIRATIONS_PER_MINUTE','SECOND','UNIT') not null,
        primary key (id)
    ) engine=InnoDB;

    create table system_datum (
        id integer not null auto_increment,
        primary key (id)
    ) engine=InnoDB;

    alter table doctor 
       add constraint UKn9b856qpp69i04wlbddu7ep7g unique (code);

    alter table doctor 
       add constraint UK3q0j5r6i4e9k3afhypo6uljph unique (user_id);

    alter table doctor_area 
       add constraint UKhf4jec5dpjs44uuo738seqhdl unique (doctor_id, evaluation_area_id);

    alter table evaluation_area 
       add constraint UK9y1u0sv0hoawv61kbtudlxstb unique (code_area);

    alter table evaluation_area 
       add constraint UKa38l0qh4fehxc0otv5wikqc7o unique (name);

    alter table image_diagnostic 
       add constraint UK7roc10a84sf9wg32rnlog9ytt unique (doctor_id, medical_img_id);

    alter table image_doctor_diagnostics 
       add constraint UKs10hymkflocpphai8gji5r03p unique (image_diagnostic_id, medical_diagnostic_id);

    alter table medical_img 
       add constraint UKlgin6wppxj4lrgulinwl0inu8 unique (image_key);

    alter table medical_diagnostic 
       add constraint UKdtomw9perh7wpymaos2jqpg7a unique (diagnostic_code);

    alter table patient 
       add constraint UKqvw1rf36r7x0c5pgr9e19vyd6 unique (document);

    alter table patient 
       add constraint UK6i3fp8wcdxk473941mbcvdao4 unique (user_id);

    alter table clinical_record 
       add constraint FKlgqmdpi8y1euv74nln3gow71h 
       foreign key (patient_id) 
       references patient (id);

    alter table dataset 
       add constraint FK9yl08ipi571j9fpjday2tp33s 
       foreign key (medical_diagnostic_id) 
       references medical_diagnostic (id);

    alter table dataset 
       add constraint FKk40ukx97jatcroi8hh08hw54y 
       foreign key (medical_image_type_id) 
       references medical_image_type (id);

    alter table dataset_category 
       add constraint FKjwe47x4e7tbasluo4dv902v1l 
       foreign key (dataset_id) 
       references dataset (id);

    alter table diagnosis 
       add constraint FKjjorwvb6cyu4rw074lq1g09pm 
       foreign key (clinical_record_id) 
       references clinical_record (id);

    alter table diagnosis 
       add constraint FKh6voc5kd5rxi0h4c76t4j5fl1 
       foreign key (medical_diagnostic_id) 
       references medical_diagnostic (id);

    alter table diagnostic_category_dataset 
       add constraint FKfwp0fwau5lqqwdtngjvu4r4on 
       foreign key (dataset_category_id) 
       references dataset_category (id);

    alter table diagnostic_category_dataset 
       add constraint FKlv9qfi54h14h0v8thh0idh5h 
       foreign key (medical_diagnostic_id) 
       references medical_diagnostic (id);

    alter table doctor_area 
       add constraint FKjd84yqhj2l5d0dd3bvepb4dxr 
       foreign key (doctor_id) 
       references doctor (id);

    alter table doctor_area 
       add constraint FKb8o4efvjjyhgnup8ocaw77i1k 
       foreign key (evaluation_area_id) 
       references evaluation_area (id);

    alter table image_diagnostic 
       add constraint FKp7druxm3j26agaow3x8pny9bc 
       foreign key (dataset_category_id) 
       references dataset_category (id);

    alter table image_diagnostic 
       add constraint FK5kawcclwalsa59xhkrvnvq8ab 
       foreign key (doctor_id) 
       references doctor (id);

    alter table image_diagnostic 
       add constraint FK6k58e2gnvml9cblf7lgkjuhq1 
       foreign key (medical_img_id) 
       references medical_img (id);

    alter table image_doctor_diagnostics 
       add constraint FK2sxek7q7fr2j2onnnqiowxxh 
       foreign key (image_diagnostic_id) 
       references image_diagnostic (id);

    alter table image_doctor_diagnostics 
       add constraint FKsimjx1mtbadk8osftqxsxicn8 
       foreign key (medical_diagnostic_id) 
       references medical_diagnostic (id);

    alter table medical_img 
       add constraint FKrm7xby8kf45loj4blaclt0ula 
       foreign key (clinical_record_id) 
       references clinical_record (id);

    alter table medical_img 
       add constraint FKq11ifi5ex4dph7tityxel15ol 
       foreign key (medical_image_type_id) 
       references medical_image_type (id);

    alter table medical_img 
       add constraint FKb5bbsky7gj8rkmngp8i3m4feo 
       foreign key (id) 
       references system_datum (id);

    alter table medical_diagnostic 
       add constraint FK2p6xl49j91l53o53k9wjxymel 
       foreign key (parent_diagnostic_id) 
       references medical_diagnostic (id);

    alter table medical_image_type 
       add constraint FK48w2sp7955iwseg0h2m6h07ld 
       foreign key (evaluation_area_id) 
       references evaluation_area (id);

    alter table patient_datum 
       add constraint FKln16kqwhxwjiw16y7sfrn4t2r 
       foreign key (clinical_record_id) 
       references clinical_record (id);

    alter table patient_datum 
       add constraint FKbvpeiocp5u4osm723snwtwbha 
       foreign key (primitive_datum_id) 
       references primitive_datum (id);

    alter table primitive_datum 
       add constraint FKt660t5nhy1mfylg7oqdl0rba1 
       foreign key (id) 
       references system_datum (id);
