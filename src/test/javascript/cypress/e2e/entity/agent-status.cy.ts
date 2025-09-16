import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('AgentStatus e2e test', () => {
  const agentStatusPageUrl = '/noospherehub/agent-status';
  const agentStatusPageUrlPattern = new RegExp('/noospherehub/agent-status(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const agentStatusSample = { createdAt: '2025-09-15T14:55:55.813Z' };

  let agentStatus;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/services/noospherehub/api/agent-statuses+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/services/noospherehub/api/agent-statuses').as('postEntityRequest');
    cy.intercept('DELETE', '/services/noospherehub/api/agent-statuses/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (agentStatus) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/services/noospherehub/api/agent-statuses/${agentStatus.id}`,
      }).then(() => {
        agentStatus = undefined;
      });
    }
  });

  it('AgentStatuses menu should load AgentStatuses page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('noospherehub/agent-status');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('AgentStatus').should('exist');
    cy.url().should('match', agentStatusPageUrlPattern);
  });

  describe('AgentStatus page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(agentStatusPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create AgentStatus page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/noospherehub/agent-status/new$'));
        cy.getEntityCreateUpdateHeading('AgentStatus');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentStatusPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/services/noospherehub/api/agent-statuses',
          body: agentStatusSample,
        }).then(({ body }) => {
          agentStatus = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/services/noospherehub/api/agent-statuses+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/services/noospherehub/api/agent-statuses?page=0&size=20>; rel="last",<http://localhost/services/noospherehub/api/agent-statuses?page=0&size=20>; rel="first"',
              },
              body: [agentStatus],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(agentStatusPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details AgentStatus page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('agentStatus');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentStatusPageUrlPattern);
      });

      it('edit button click should load edit AgentStatus page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AgentStatus');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentStatusPageUrlPattern);
      });

      it('edit button click should load edit AgentStatus page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('AgentStatus');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentStatusPageUrlPattern);
      });

      it('last delete button click should delete instance of AgentStatus', () => {
        cy.intercept('GET', '/services/noospherehub/api/agent-statuses/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('agentStatus').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', agentStatusPageUrlPattern);

        agentStatus = undefined;
      });
    });
  });

  describe('new AgentStatus page', () => {
    beforeEach(() => {
      cy.visit(`${agentStatusPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('AgentStatus');
    });

    it('should create an instance of AgentStatus', () => {
      cy.get(`[data-cy="createdAt"]`).type('2025-09-15T07:34');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2025-09-15T07:34');

      cy.get(`[data-cy="lastKeepAliveAt"]`).type('2025-09-15T18:32');
      cy.get(`[data-cy="lastKeepAliveAt"]`).blur();
      cy.get(`[data-cy="lastKeepAliveAt"]`).should('have.value', '2025-09-15T18:32');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        agentStatus = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', agentStatusPageUrlPattern);
    });
  });
});
