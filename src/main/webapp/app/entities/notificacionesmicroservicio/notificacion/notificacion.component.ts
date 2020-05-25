import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { INotificacion } from 'app/shared/model/notificacionesmicroservicio/notificacion.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { NotificacionService } from './notificacion.service';
import { NotificacionDeleteDialogComponent } from './notificacion-delete-dialog.component';

@Component({
  selector: 'jhi-notificacion',
  templateUrl: './notificacion.component.html'
})
export class NotificacionComponent implements OnInit, OnDestroy {
  notificacions?: INotificacion[];
  eventSubscriber?: Subscription;
  totalItems = 0;
  itemsPerPage = ITEMS_PER_PAGE;
  page!: number;
  predicate!: string;
  ascending!: boolean;
  ngbPaginationPage = 1;

  constructor(
    protected notificacionService: NotificacionService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal
  ) {}

  loadPage(page?: number): void {
    const pageToLoad: number = page || this.page;

    this.notificacionService
      .query({
        page: pageToLoad - 1,
        size: this.itemsPerPage,
        sort: this.sort()
      })
      .subscribe(
        (res: HttpResponse<INotificacion[]>) => this.onSuccess(res.body, res.headers, pageToLoad),
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
    this.registerChangeInNotificacions();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: INotificacion): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInNotificacions(): void {
    this.eventSubscriber = this.eventManager.subscribe('notificacionListModification', () => this.loadPage());
  }

  delete(notificacion: INotificacion): void {
    const modalRef = this.modalService.open(NotificacionDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.notificacion = notificacion;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected onSuccess(data: INotificacion[] | null, headers: HttpHeaders, page: number): void {
    this.totalItems = Number(headers.get('X-Total-Count'));
    this.page = page;
    this.router.navigate(['/notificacion'], {
      queryParams: {
        page: this.page,
        size: this.itemsPerPage,
        sort: this.predicate + ',' + (this.ascending ? 'asc' : 'desc')
      }
    });
    this.notificacions = data || [];
  }

  protected onError(): void {
    this.ngbPaginationPage = this.page;
  }
}
