export interface IUsuario {
  id?: number;
  nombre?: string;
  apellido?: string;
  direccion?: string;
  userId?: number;
}

export class Usuario implements IUsuario {
  constructor(public id?: number, public nombre?: string, public apellido?: string, public direccion?: string, public userId?: number) {}
}
