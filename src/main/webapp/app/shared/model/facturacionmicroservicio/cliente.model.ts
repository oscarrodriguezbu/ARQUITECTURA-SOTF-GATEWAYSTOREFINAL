export interface ICliente {
  id?: number;
  nombre?: string;
  apellido?: string;
  identificacion?: string;
  facturaId?: number;
}

export class Cliente implements ICliente {
  constructor(
    public id?: number,
    public nombre?: string,
    public apellido?: string,
    public identificacion?: string,
    public facturaId?: number
  ) {}
}
