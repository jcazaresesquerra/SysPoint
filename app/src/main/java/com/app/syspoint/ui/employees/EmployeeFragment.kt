package com.app.syspoint.ui.employees

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.syspoint.R
import com.app.syspoint.databinding.FragmentEmployeeBinding
import com.app.syspoint.models.sealed.EmployeeLoadingViewState
import com.app.syspoint.models.sealed.EmployeeViewState
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.ui.employees.activities.ActualizarEmpleadoActivity
import com.app.syspoint.ui.employees.activities.RegistarEmpleadoActivity
import com.app.syspoint.ui.employees.adapters.EmployeeListAdapter
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.click
import com.app.syspoint.utils.onFocusChange
import com.app.syspoint.utils.onQueryText
import com.app.syspoint.utils.setInvisible
import com.app.syspoint.utils.setVisible
import com.app.syspoint.viewmodel.employee.EmployeeViewModel
import kotlinx.android.synthetic.main.fragment_employee.lyt_empleados
import timber.log.Timber

private const val TAG = "EmployeeFragment"

class EmployeeFragment: Fragment() {

    private lateinit var binding: FragmentEmployeeBinding
    private lateinit var viewModel: EmployeeViewModel
    private lateinit var adapter: EmployeeListAdapter

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentEmployeeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this)[EmployeeViewModel::class.java]
        viewModel.employeeViewState.observe(viewLifecycleOwner, ::renderViewState)
        viewModel.employeeProgressViewState.observe(viewLifecycleOwner, ::renderLoadingViewState)
        viewModel.setUpEmployees()
        setUpListeners()
    }

    /*override fun onResume() {
        super.onResume()
        viewModel.refreshEmployees()
    }*/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_empleado_fragment, menu)
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView onFocusChange { _, hasFocus ->
            if (!hasFocus) {
                searchMenuItem.collapseActionView()
                searchView.setQuery("", false)
            }
        }

        searchView.onQueryText({
            adapter.filter.filter(it)
        },{
            adapter.filter.filter(it)
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when(item.itemId) {
            R.id.syncEmpleados -> {
                Timber.tag(TAG).d("sync employees -> click")
                viewModel.checkConnectivity()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun renderLoadingViewState(employeeLoadingViewState: EmployeeLoadingViewState) {
        when(employeeLoadingViewState) {
            is EmployeeLoadingViewState.LoadingStartState -> {
                binding.rlprogressEmpleados.setVisible()
            }
            is EmployeeLoadingViewState.LoadingFinishState -> {
                binding.rlprogressEmpleados.setInvisible()
            }
        }
    }

    private fun renderViewState(employeeViewState: EmployeeViewState) {
        when(employeeViewState) {
            is EmployeeViewState.ShowProgressState -> {
                progressDialog = ProgressDialog(activity)
                progressDialog.setMessage("Espere un momento")
                progressDialog.setCancelable(false)
                progressDialog.show()
            }
            is EmployeeViewState.DismissProgressState -> {
                if (::progressDialog.isInitialized)
                    progressDialog.dismiss()
            }
            is EmployeeViewState.NetworkDisconnectedState -> {
                //showDialogNotConnectionInternet()
            }
            is EmployeeViewState.SetUpEmployeesState -> {
                initRecyclerView(employeeViewState.data)
            }
            is EmployeeViewState.RefreshEmployeesState -> {
                refreshRecyclerView(employeeViewState.data)
            }
            is EmployeeViewState.LoadingStartState -> {
                binding.rlprogressEmpleados.setVisible()
            }
            is EmployeeViewState.LoadingFinishState -> {
                binding.rlprogressEmpleados.setInvisible()
            }
            is EmployeeViewState.GetEmployeesState -> {
                refreshRecyclerView(employeeViewState.data)
            }
            is EmployeeViewState.CallEmployeeState -> {
                call(employeeViewState.number)
            }
            is EmployeeViewState.SendEmailState -> {
                sendEmail()
            }
            is EmployeeViewState.ShowEditEmployeeState -> {
                showEditEmployee(employeeViewState.employeeId)
            }
            is EmployeeViewState.CanNotEditEmployeeState -> {
                Toast.makeText(
                    requireContext(),
                    "No tienes privilegios para esta area",
                    Toast.LENGTH_LONG
                ).show()
            }
           
        }
    }

    private fun setUpListeners() {
        binding.floatingActionButton click {
            Timber.tag(TAG).d("floatingActionButton -> click")
            binding.floatingActionButton.isEnabled = false
            showRegisterEmployee()
            binding.floatingActionButton.isEnabled = true
        }
    }

    private fun refreshRecyclerView(employees: List<EmployeeBox?>) {
        if (::adapter.isInitialized) {
            adapter.setData(employees)
            if (employees.isNotEmpty()) {
                lyt_empleados.setInvisible()
            } else {
                lyt_empleados.setVisible()
            }
        }
    }
    
    private fun initRecyclerView(employees: List<EmployeeBox?>) {
        if (employees.isNotEmpty()) {
            binding.lytEmpleados.setInvisible()
        } else {
            binding.lytEmpleados.setVisible()
        }

        binding.rvListaEmpleados.setHasFixedSize(true)

        val manager = LinearLayoutManager(activity)
        binding.rvListaEmpleados.layoutManager = manager

        adapter = EmployeeListAdapter(employees, object : EmployeeListAdapter.OnItemClickListener {
            override fun onItemClick(employeeBean: EmployeeBox?) {
                Timber.tag(TAG).d("initRecyclerView -> EmployeeListAdapter -> click")
                showSelectionFunction(employeeBean)
            }
        })
        binding.rvListaEmpleados.adapter = adapter
    }

    private fun showSelectionFunction(employeeBean: EmployeeBox?) {
        val builderSingle = AlertDialog.Builder(requireContext())
        builderSingle.setIcon(R.drawable.tenet_icon)
        builderSingle.setTitle("Seleccionar opción")

        val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        arrayAdapter.add("Editar")
        arrayAdapter.add("Llamar")
        arrayAdapter.add("Enviar email")

        builderSingle.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
            val name = arrayAdapter.getItem(which)
            employeeBean?.let {
                viewModel.handleSelection(name, employeeBean)
            }
            dialog.dismiss()
        }
        builderSingle.show()
    }
    
    private fun showRegisterEmployee() {
        val intent = Intent(context, RegistarEmpleadoActivity::class.java)
        startActivity(intent)
    }

    private fun showDialogNotConnectionInternet() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_warning)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(R.id.bt_close) as AppCompatButton) click  {
            viewModel.getData()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }
    
    private fun showEditEmployee(id: String) {
        Timber.tag(TAG).d("showEditEmployee -> %s", id)

        val params = HashMap<String, String>()
        params[Actividades.PARAM_1] = id
        Actividades.getSingleton(activity, ActualizarEmpleadoActivity::class.java).muestraActividad(params)
    }
    
    private fun call(number: String) {
        Timber.tag(TAG).d("call -> %s", number)

        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CALL_PHONE), Constants.REQUEST_PERMISSION_CALL)
            return
        }
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$number")
        startActivity(callIntent)
    }
    
    private fun sendEmail() {
        Timber.tag(TAG).d("sendEmail")

        val TO = arrayOf("someone@gmail.com")
        val CC = arrayOf("xyz@gmail.com")
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.type = "text/plain"

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
        emailIntent.putExtra(Intent.EXTRA_CC, CC)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here")

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            requireActivity().finish()
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                activity,
                "There is no email client installed.", Toast.LENGTH_SHORT
            ).show()
        }
    }
}