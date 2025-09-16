import React, { useEffect, useState } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, useLocation } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, reset } from './container.reducer';

export const Container = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const containerList = useAppSelector(state => state.noospherehub.container.entities);
  const loading = useAppSelector(state => state.noospherehub.container.loading);
  const links = useAppSelector(state => state.noospherehub.container.links);
  const updateSuccess = useAppSelector(state => state.noospherehub.container.updateSuccess);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const resetAll = () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
    });
    dispatch(getEntities({}));
  };

  useEffect(() => {
    resetAll();
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      resetAll();
    }
  }, [updateSuccess]);

  useEffect(() => {
    getAllEntities();
  }, [paginationState.activePage]);

  const handleLoadMore = () => {
    if ((window as any).pageYOffset > 0) {
      setPaginationState({
        ...paginationState,
        activePage: paginationState.activePage + 1,
      });
    }
  };

  useEffect(() => {
    if (sorting) {
      getAllEntities();
      setSorting(false);
    }
  }, [sorting]);

  const sort = p => () => {
    dispatch(reset());
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
    setSorting(true);
  };

  const handleSyncList = () => {
    resetAll();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="container-heading" data-cy="ContainerHeading">
        <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.home.title">Containers</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/noospherehub/container/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.home.createLabel">Create new Container</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={containerList ? containerList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {containerList && containerList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.id">Id</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('name')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.name">Name</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                  </th>
                  <th className="hand" onClick={sort('walletAddress')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.walletAddress">Wallet Address</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('walletAddress')} />
                  </th>
                  <th className="hand" onClick={sort('price')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.price">Price</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('price')} />
                  </th>
                  <th className="hand" onClick={sort('statusCode')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.statusCode">Status Code</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('statusCode')} />
                  </th>
                  <th className="hand" onClick={sort('description')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.description">Description</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                  </th>
                  <th className="hand" onClick={sort('parameters')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.parameters">Parameters</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('parameters')} />
                  </th>
                  <th className="hand" onClick={sort('createdAt')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.createdAt">Created At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                  </th>
                  <th className="hand" onClick={sort('updatedAt')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.updatedAt">Updated At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                  </th>
                  <th>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.createdByUser">Created By User</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.updatedByUser">Updated By User</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {containerList.map((container, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/noospherehub/container/${container.id}`} color="link" size="sm">
                        {container.id}
                      </Button>
                    </td>
                    <td>{container.name}</td>
                    <td>{container.walletAddress}</td>
                    <td>{container.price}</td>
                    <td>{container.statusCode}</td>
                    <td>{container.description}</td>
                    <td>{container.parameters}</td>
                    <td>{container.createdAt ? <TextFormat type="date" value={container.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                    <td>{container.updatedAt ? <TextFormat type="date" value={container.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                    <td>{container.createdByUser ? container.createdByUser.id : ''}</td>
                    <td>{container.updatedByUser ? container.updatedByUser.id : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button
                          tag={Link}
                          to={`/noospherehub/container/${container.id}`}
                          color="info"
                          size="sm"
                          data-cy="entityDetailsButton"
                        >
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button
                          tag={Link}
                          to={`/noospherehub/container/${container.id}/edit`}
                          color="primary"
                          size="sm"
                          data-cy="entityEditButton"
                        >
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button
                          onClick={() => (window.location.href = `/noospherehub/container/${container.id}/delete`)}
                          color="danger"
                          size="sm"
                          data-cy="entityDeleteButton"
                        >
                          <FontAwesomeIcon icon="trash" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.delete">Delete</Translate>
                          </span>
                        </Button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && (
              <div className="alert alert-warning">
                <Translate contentKey="nooSphereHubApp.nooSphereHubContainer.home.notFound">No Containers found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default Container;
