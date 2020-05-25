export interface IStock {
  id?: number;
  cantidad?: number;
  productoNombre?: string;
  productoId?: number;
}

export class Stock implements IStock {
  constructor(public id?: number, public cantidad?: number, public productoNombre?: string, public productoId?: number) {}
}
