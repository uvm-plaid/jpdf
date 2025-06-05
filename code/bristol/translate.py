# TODO Add parameter for file path
file_path = 'test.txt'

print(f'test(prefix, [inputs...])')
with open(file_path, 'r') as file:
    # Number of gates, number of wires
    # TODO Maybe validate?
    header_line_1 = file.readline()
    tokens = header_line_1.strip().split(' ')
    num_gates = int(tokens[0])
    num_wires = int(tokens[1])

    # Number of inputs, number of bits per input
    header_line_2 = file.readline()
    tokens = header_line_2.strip().split(' ')
    num_inputs = int(tokens[0])
    assert len(tokens) == num_inputs + 1
    input_lengths = [int(x) for x in tokens[1:]]

    # Number of outputs, number of bits per output
    header_line_3 = file.readline()
    tokens = header_line_3.strip().split(' ')
    num_outputs = int(tokens[0])
    assert len(tokens) == num_outputs + 1
    output_lengths = [int(x) for x in tokens[1:]]

    # Should be blank
    header_line_4 = file.readline()
    assert header_line_4.strip() == ''

    gate_counter = 0
    while True:
        gate_counter += 1
        line = file.readline()
        if not line: break
        line = line.strip().split(' ')
        gate_type = line[-1]
        n_in_wires = int(line[0])
        in_wires = [int(x) for x in line[2:n_in_wires + 2]]
        n_out_wires = int(line[1])
        out_wires = [int(x) for x in line[n_in_wires + 2:-1]]
        if gate_type == 'AND':
            print(f'andgmw(prefix ++ "_w{out_wires[0]}", prefix ++ "_w{in_wires[0]}", prefix ++ "_w{in_wires[1]}")', end='')
        # TODO Add support for other gate types
        else:
            raise ValueError(f'Unknown gate type: {gate_type}')
        if gate_counter < num_gates:
            print(';')
        else:
            print('')
