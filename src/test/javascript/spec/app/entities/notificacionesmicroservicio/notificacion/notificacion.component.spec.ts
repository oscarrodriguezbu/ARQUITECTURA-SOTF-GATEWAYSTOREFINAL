import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Data } from '@angular/router';

import { GatewaystoreTestModule } from '../../../../test.module';
import { NotificacionComponent } from 'app/entities/notificacionesmicroservicio/notificacion/notificacion.component';
import { NotificacionService } from 'app/entities/notificacionesmicroservicio/notificacion/notificacion.service';
import { Notificacion } from 'app/shared/model/notificacionesmicroservicio/notificacion.model';

describe('Component Tests', () => {
  describe('Notificacion Management Component', () => {
    let comp: NotificacionComponent;
    let fixture: ComponentFixture<NotificacionComponent>;
    let service: NotificacionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [GatewaystoreTestModule],
        declarations: [NotificacionComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              data: {
                subscribe: (fn: (value: Data) => void) =>
                  fn({
                    pagingParams: {
                      predicate: 'id',
                      reverse: false,
                      page: 0
                    }
                  })
              }
            }
          }
        ]
      })
        .overrideTemplate(NotificacionComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(NotificacionComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(NotificacionService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Notificacion(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.notificacions && comp.notificacions[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });

    it('should load a page', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Notificacion(123)],
            headers
          })
        )
      );

      // WHEN
      comp.loadPage(1);

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.notificacions && comp.notificacions[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });

    it('should calculate the sort attribute for an id', () => {
      // WHEN
      comp.ngOnInit();
      const result = comp.sort();

      // THEN
      expect(result).toEqual(['id,desc']);
    });

    it('should calculate the sort attribute for a non-id attribute', () => {
      // INIT
      comp.ngOnInit();

      // GIVEN
      comp.predicate = 'name';

      // WHEN
      const result = comp.sort();

      // THEN
      expect(result).toEqual(['name,desc', 'id']);
    });
  });
});
