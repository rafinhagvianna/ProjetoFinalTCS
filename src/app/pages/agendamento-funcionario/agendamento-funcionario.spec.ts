import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AgendamentoFuncionarioComponent } from './agendamento-funcionario.component';

describe('AgendamentoFuncionario', () => {
  let component: AgendamentoFuncionarioComponent;
  let fixture: ComponentFixture<AgendamentoFuncionarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AgendamentoFuncionarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AgendamentoFuncionarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
