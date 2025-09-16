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

import { getEntities, reset } from './agent.reducer';

export const Agent = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );
  const [sorting, setSorting] = useState(false);

  const agentList = useAppSelector(state => state.noospherehub.agent.entities);
  const loading = useAppSelector(state => state.noospherehub.agent.loading);
  const links = useAppSelector(state => state.noospherehub.agent.links);
  const updateSuccess = useAppSelector(state => state.noospherehub.agent.updateSuccess);

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
      <h2 id="agent-heading" data-cy="AgentHeading">
        <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.home.title">Agents</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link
            to="/noospherehub/agent/new"
            className="btn btn-primary jh-create-entity"
            id="jh-create-entity"
            data-cy="entityCreateButton"
          >
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.home.createLabel">Create new Agent</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={agentList ? agentList.length : 0}
          next={handleLoadMore}
          hasMore={paginationState.activePage - 1 < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {agentList && agentList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th className="hand" onClick={sort('id')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.id">Id</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                  </th>
                  <th className="hand" onClick={sort('name')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.name">Name</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                  </th>
                  <th className="hand" onClick={sort('apiUrl')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.apiUrl">Api Url</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('apiUrl')} />
                  </th>
                  <th className="hand" onClick={sort('apiKey')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.apiKey">Api Key</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('apiKey')} />
                  </th>
                  <th className="hand" onClick={sort('statusCode')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.statusCode">Status Code</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('statusCode')} />
                  </th>
                  <th className="hand" onClick={sort('description')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.description">Description</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('description')} />
                  </th>
                  <th className="hand" onClick={sort('createdAt')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.createdAt">Created At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                  </th>
                  <th className="hand" onClick={sort('updatedAt')}>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.updatedAt">Updated At</Translate>{' '}
                    <FontAwesomeIcon icon={getSortIconByFieldName('updatedAt')} />
                  </th>
                  <th>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.createdByUser">Created By User</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th>
                    <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.updatedByUser">Updated By User</Translate>{' '}
                    <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {agentList.map((agent, i) => (
                  <tr key={`entity-${i}`} data-cy="entityTable">
                    <td>
                      <Button tag={Link} to={`/noospherehub/agent/${agent.id}`} color="link" size="sm">
                        {agent.id}
                      </Button>
                    </td>
                    <td>{agent.name}</td>
                    <td>{agent.apiUrl}</td>
                    <td>{agent.apiKey}</td>
                    <td>{agent.statusCode}</td>
                    <td>{agent.description}</td>
                    <td>{agent.createdAt ? <TextFormat type="date" value={agent.createdAt} format={APP_DATE_FORMAT} /> : null}</td>
                    <td>{agent.updatedAt ? <TextFormat type="date" value={agent.updatedAt} format={APP_DATE_FORMAT} /> : null}</td>
                    <td>{agent.createdByUser ? agent.createdByUser.id : ''}</td>
                    <td>{agent.updatedByUser ? agent.updatedByUser.id : ''}</td>
                    <td className="text-end">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`/noospherehub/agent/${agent.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        <Button tag={Link} to={`/noospherehub/agent/${agent.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                          <FontAwesomeIcon icon="pencil-alt" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.edit">Edit</Translate>
                          </span>
                        </Button>
                        <Button
                          onClick={() => (window.location.href = `/noospherehub/agent/${agent.id}/delete`)}
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
                <Translate contentKey="nooSphereHubApp.nooSphereHubAgent.home.notFound">No Agents found</Translate>
              </div>
            )
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default Agent;
