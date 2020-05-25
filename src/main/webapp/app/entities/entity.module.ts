import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'usuario',
        loadChildren: () => import('./usuario/usuario.module').then(m => m.GatewaystoreUsuarioModule)
      },
      {
        path: 'notificacion',
        loadChildren: () =>
          import('./notificacionesmicroservicio/notificacion/notificacion.module').then(
            m => m.NotificacionesmicroservicioNotificacionModule
          )
      },
      {
        path: 'producto',
        loadChildren: () => import('./inventariomicroservicio/producto/producto.module').then(m => m.InventariomicroservicioProductoModule)
      },
      {
        path: 'stock',
        loadChildren: () => import('./inventariomicroservicio/stock/stock.module').then(m => m.InventariomicroservicioStockModule)
      },
      {
        path: 'factura',
        loadChildren: () => import('./facturacionmicroservicio/factura/factura.module').then(m => m.FacturacionmicroservicioFacturaModule)
      },
      {
        path: 'factura-detalle',
        loadChildren: () =>
          import('./facturacionmicroservicio/factura-detalle/factura-detalle.module').then(
            m => m.FacturacionmicroservicioFacturaDetalleModule
          )
      },
      {
        path: 'cliente',
        loadChildren: () => import('./facturacionmicroservicio/cliente/cliente.module').then(m => m.FacturacionmicroservicioClienteModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ]
})
export class GatewaystoreEntityModule {}
