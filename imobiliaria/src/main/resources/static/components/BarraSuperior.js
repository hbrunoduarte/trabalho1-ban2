import { ref, onMounted } from 'https://unpkg.com/vue@3/dist/vue.esm-browser.js'

export default {
props: {
    bordaInferior: { type: Boolean, default: false }
},
setup() {
    const isLogado = ref(false)
    const isCliente = ref(false)

    onMounted(() => {
        const usuario = localStorage.getItem('usuario')
        const id = localStorage.getItem('id_usuario')
        isLogado.value = usuario !== null && id !== null
        isCliente.value = usuario === 'CLIENTE'
    })

    async function deslogar() {
        try {
            const response = await fetch('/api/login/logout', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            })

            if (!response.ok) {
                alert('Não foi possível fazer logout.')
                return
            }

            localStorage.removeItem('usuario')
            localStorage.removeItem('id_usuario')

            isLogado.value = false
            
            window.location.href = '/login'

        } catch (error) {
            console.error("Erro ao deslogar:", error)
        }
    }

    return { deslogar, isLogado, isCliente }
},
template: `
<div id="barra-superior-principal" :class="{ 'barra-com-borda': bordaInferior }">
    
    <a class="botao-barra-principal texto-logo" href="/" >
        <i class="ph-fill ph-house-line icones"></i>
        <h1>Imobiliária</h1>
    </a>

    <div class="bprincipal-botoes-navegacao">

        <a href="/busca" class="botao-barra-principal botao-area-login" v-if="isLogado">
            <i class="ph ph-magnifying-glass icones"></i>
            <span class="texto-botao-barra-login">BUSCA</span>
        </a>

        <a href="/painel" class="botao-barra-principal botao-area-login" v-if="isLogado">
            <i class="ph ph-article icones"></i>
            <span class="texto-botao-barra-login">PAINEL</span>
        </a>
        
        <a href="/perfil" class="botao-barra-principal botao-area-login" v-if="isLogado">
            <i class="ph ph-user icones"></i>
            <span class="texto-botao-barra-login">MEUS DADOS</span>
        </a>

        <a href="/login" class="botao-barra-principal botao-area-login" v-if="!isLogado">
            <i class="ph ph-user-circle icones"></i>
            <span class="texto-botao-barra-login">ÁREA DE LOGIN</span>
        </a>
        <a href="#" class="botao-barra-principal botao-area-login" @click.prevent="deslogar()" v-else>
            <i class="ph ph-sign-out icones"></i>
            <span class="texto-botao-barra-login">LOGOUT</span>
        </a>
    </div>
</div>
`
}