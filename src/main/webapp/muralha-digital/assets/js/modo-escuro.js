(function() {
  'use strict';

  function getStored() { try { return localStorage.getItem('tema-muralha'); } catch(e) { return null; } }
  function setStored(t) { try { localStorage.setItem('tema-muralha', t); } catch(e) {} }

  function apply(tema) {
    document.documentElement.setAttribute('data-bs-theme', tema);
    var btn = document.getElementById('btnTema');
    if (btn) {
      btn.innerHTML = tema === 'dark'
        ? '<i class="bi bi-sun"></i>'
        : '<i class="bi bi-moon-stars"></i>';
      btn.title = tema === 'dark' ? 'Modo Claro' : 'Modo Escuro';
    }
  }

  var tema = getStored() || 'light';
  apply(tema);

  window.addEventListener('DOMContentLoaded', function() {
    apply(getStored() || 'light');
    var btn = document.getElementById('btnTema');
    if (btn) {
      btn.addEventListener('click', function() {
        var atual = document.documentElement.getAttribute('data-bs-theme') || 'light';
        var novo = atual === 'dark' ? 'light' : 'dark';
        setStored(novo);
        apply(novo);
      });
    }
  });
})();
