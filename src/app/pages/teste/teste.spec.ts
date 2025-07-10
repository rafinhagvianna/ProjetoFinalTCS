import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Teste } from './teste';

describe('Teste', () => {
  let component: Teste;
  let fixture: ComponentFixture<Teste>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Teste]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Teste);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
