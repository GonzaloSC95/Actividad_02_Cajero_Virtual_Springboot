create database cajero_2024;
use cajero_2024;

create table cuentas
(
   id_cuenta int not null primary key,
	saldo double not null,
	tipo_cuenta varchar(20) not null check (tipo_cuenta in ('AHORRO','CORRIENTE','JUVENIL', 'NOMINA'))
);

INSERT INTO CUENTAS VALUES(1000, 2000,'AHORRO');
INSERT INTO CUENTAS VALUES(2000, 12000,'CORRIENTE');

CREATE TABLE MOVIMIENTOS
(
   ID_MOVIMIENTO INT NOT NULL auto_increment PRIMARY KEY,
	ID_CUENTA INT NOT NULL,
	FECHA DATETIME DEFAULT TIMESTAMP,
	CANTIDAD DOUBLE,
	OPERACION VARCHAR(45) null check (OPERACION in ('EXTRACCION','INGRESO')),
	FOREIGN KEY(ID_CUENTA) REFERENCES CUENTAS(ID_CUENTA)
);

-- drop user ucajero;
CREATE USER ucajero identified by 'ucajero';
GRANT ALL PRIVILEGES ON cajero_2024.* TO ucajero;
