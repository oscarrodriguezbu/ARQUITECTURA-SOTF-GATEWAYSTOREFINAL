import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IFacturaDetalle } from 'app/shared/model/facturacionmicroservicio/factura-detalle.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { FacturaDetalleService } from './factura-detalle.service';
import { FacturaDetalleDeleteDialogComponent } from './factura-detalle-delete-dialog.component';

@Component({
  selector: 'jhi-factura-detalle',
  templateUrl: './factura-detalle.component.html'
})
export class FacturaDetalleComponent implements OnInit, OnDestroy {
  facturaDetalles?: IFacturaDetalle[];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected facturaDetalleService: FacturaDetalleService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number): void {
    const pageToLoad: number = page || this.page;

    this.facturaDetalleService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe(
        (res: HttpResponse<IFacturaDetalle[]>) => this.onSuccess(res.body, res.headers, pageToLoad),
        () => this.onError()
      );
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(data => {
      this.page = data.pagingParams.page;
      this.ascending = data.pagingParams.ascending;
      this.predicate = data.pagingParams.predicate;
      this.ngbPaginationPage = data.pagingParams.page;
      this.loadPage();
    });
    this.registerChangeInFacturaDetalles();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: IFacturaDetalle): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInFacturaDetalles(): void {
    this.eventSubscriber = this.eventManager.subscribe('facturaDetalleListModification', () => this.loadPage());
  }

  delete(facturaDetalle: IFacturaDetalle): void {
    const modalRef = this.modalService.open(FacturaDetalleDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.facturaDetalle = facturaDetalle;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: IFacturaDetalle[] | null, headers: HttpHeaders, page: number): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.router.navigate(['/factura-detalle'], {
      queryParams: {
        page: this.page,
        size: this.itemsPerPage,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc')
      }
    });
    this.facturaDetalles = data || [];
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page;
  }
}
