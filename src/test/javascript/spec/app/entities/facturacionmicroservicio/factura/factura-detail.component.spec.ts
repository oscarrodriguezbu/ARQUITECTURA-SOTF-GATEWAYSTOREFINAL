import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GatewaystoreTestModule } from '../../../../test.module';
import { FacturaDetailComponent } from 'app/entities/facturacionmicroservicio/factura/factura-detail.component';
import { Factura } from 'app/shared/model/facturacionmicroservicio/factura.model';

describe('Component Tests', () => {
  describe('Factura Management Detail Component', () => {
    let comp: FacturaDetailComponent;
    let fixture: ComponentFixture<FacturaDetailComponent>;
    const route = ({ data: of({ factura: new Factura(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [GatewaystoreTestModule],
        declarations: [FacturaDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(FacturaDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(FacturaDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load factura on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.factura).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
